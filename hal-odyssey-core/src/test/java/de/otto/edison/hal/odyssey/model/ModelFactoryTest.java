package de.otto.edison.hal.odyssey.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.HalRepresentation;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.otto.edison.hal.Embedded.embedded;
import static de.otto.edison.hal.Link.*;
import static de.otto.edison.hal.Links.emptyLinks;
import static de.otto.edison.hal.Links.linkingTo;
import static de.otto.edison.hal.odyssey.model.PrettyPrinter.prettyPrintJson;
import static de.otto.edison.hal.odyssey.model.ResponseModel.EMPTY_RESPONSE;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ModelFactoryTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void shouldCreateEmptyMainModel() {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());

        // when
        final Map<String, ?> model = modelFactory.emptyMainModel();

        // then
        assertThat(model.get("currentUrl")).isEqualTo("http://");
        assertThat(model.get("pager")).isEqualTo(PagerModel.UNAVAILABLE);
        assertThat(model.get("response")).isEqualTo(EMPTY_RESPONSE);
        assertThat(model.get("self")).isNull();
        assertThat(model.get("customAttributes")).isEqualTo(emptyMap());
        assertThat(model.get("linkTabs")).isEqualTo(emptyList());
        assertThat(model.get("embeddedTabs")).isEqualTo(emptyList());
        final LinkTabModel curiTab = (LinkTabModel) model.get("curiTab");
        assertThat(curiTab.index).isEqualTo(0);
        assertThat(curiTab.linkRelation.rel).isEqualTo("curies");
        assertThat(curiTab.linkRelation.href).isEqualTo("https://tools.ietf.org/html/draft-kelly-json-hal-08");
        assertThat(curiTab.linkRelation.description).isNotBlank();
        assertThat(curiTab.links).isEmpty();
    }

    @Test
    public void shouldCreateMainModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(linkingTo().self("http://example.com").build());
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        assertThat(model.get("index")).isEqualTo(0);
        assertThat(model.get("currentUrl")).isEqualTo("http://example.com");
        assertThat(model).containsOnlyKeys(
                "index", "currentUrl", "pager", "response", "self",
                "customAttributes", "linkTabs", "embeddedTabs", "curiTab"
        );
    }

    @Test
    public void shouldCreateSelfModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(linkingTo().self("http://example.com").build());
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final SelfModel self = (SelfModel) model.get("self");
        assertThat(self.link.href).isEqualTo("http://example.com");
        assertThat(self.linkRelation.rel).isEqualTo("self");
        assertThat(self.linkRelation.href).isEqualTo("https://www.iana.org/assignments/link-relations/link-relations.xhtml");
        assertThat(self.linkRelation.description).isNotBlank();
    }

    @Test
    public void shouldCreatePagerModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(linkingTo()
                .single(link("first", "http://example.com/1"))
                .single(link("next", "http://example.com/3"))
                .build()
        );
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final PagerModel pager = (PagerModel) model.get("pager");
        assertThat(pager.available).isTrue();
        assertThat(pager.first.href).isEqualTo("http://example.com/1");
        assertThat(pager.prev).isNull();
        assertThat(pager.next.href).isEqualTo("http://example.com/3");
        assertThat(pager.last).isNull();
    }

    @Test
    public void shouldCreateResponseModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(linkingTo().single(self("http://example.com")).build());
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final String prettyPrintedJson = prettyPrintJson(json);
        final ResponseModel responseModel = (ResponseModel) model.get("response");
        assertThat(responseModel.headers).isEqualTo(new HttpHeaders());
        assertThat(responseModel.body).isEqualTo(prettyPrintedJson);
    }

    private static class HalAttrTest extends HalRepresentation {
        @JsonProperty
        public String foo = "42";
        @JsonProperty
        public List<String> bar = asList("4711", "0815");
    }

    @Test
    public void shouldCreateCustomAttributes() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final String json = OBJECT_MAPPER.writeValueAsString(new HalAttrTest());
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        assertThat(model.get("customAttributes")).isEqualTo(new HashMap<String,String>() {{
            put("foo", "\"42\"");
            put("bar", "[ \"4711\", \"0815\" ]");
        }});
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateLinkTabs() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(linkingTo()
                .array(
                        item("http://example.com/item/1"),
                        item("http://example.com/item/2"))
                .single(link("example", "http://example.com/example"))
                .build()
        );
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final List<LinkTabModel> linkTabs = (List<LinkTabModel>) model.get("linkTabs");
        assertThat(linkTabs).hasSize(2);
        assertThat(linkTabs.get(0).index).isEqualTo(0);
        assertThat(linkTabs.get(0).id).isNotBlank();
        assertThat(linkTabs.get(0).linkRelation.rel).isEqualTo("example");
        assertThat(linkTabs.get(0).linkRelation.href).isNull();
        assertThat(linkTabs.get(0).linkRelation.description).isNull();
        assertThat(linkTabs.get(0).links).hasSize(1);
        assertThat(linkTabs.get(0).links.get(0).href).isEqualTo("http://example.com/example");
        assertThat(linkTabs.get(1).index).isEqualTo(1);
        assertThat(linkTabs.get(1).id).isNotBlank();
        assertThat(linkTabs.get(1).linkRelation.rel).isEqualTo("item");
        assertThat(linkTabs.get(1).linkRelation.href).isEqualTo("https://www.iana.org/assignments/link-relations/link-relations.xhtml");
        assertThat(linkTabs.get(1).linkRelation.description).isNotBlank();
        assertThat(linkTabs.get(1).links).hasSize(2);
        assertThat(linkTabs.get(1).links.get(0).href).isEqualTo("http://example.com/item/1");
        assertThat(linkTabs.get(1).links.get(1).href).isEqualTo("http://example.com/item/2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateEmbeddedModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(
                emptyLinks(),
                embedded("item", singletonList(new HalAttrTest()))
        );
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final List<EmbeddedTabModel> embedded = (List<EmbeddedTabModel>) model.get("embeddedTabs");
        assertThat(embedded).hasSize(1);
        final EmbeddedTabModel tab = embedded.get(0);
        assertThat(tab.index).isEqualTo(0);
        assertThat(tab.linkRelation.rel).isEqualTo("item");
        assertThat(tab.linkRelation.href).isEqualTo("https://www.iana.org/assignments/link-relations/link-relations.xhtml");
        assertThat(tab.linkRelation.description).isNotBlank();
        assertThat(tab.items).hasSize(1);
        final Map<String, ?> item = tab.items.get(0);
        assertThat(item).containsOnlyKeys(
                "index", "pager", "self",
                "customAttributes", "linkTabs", "embeddedTabs", "curiTab"
        );
    }

    @Test
    public void shouldCreateCuriModel() throws IOException {
        // given
        final ModelFactory modelFactory = new ModelFactory(new LinkRelationService(), new ObjectMapper());
        final HalRepresentation hal = new HalRepresentation(
                linkingTo()
                        .curi("ex", "http://example.com/{rel}")
                        .build());
        final String json = OBJECT_MAPPER.writeValueAsString(hal);
        // when
        final ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        final Map<String, ?> model = modelFactory.toMainModel("http://example.com", response);

        // then
        final LinkTabModel curiTab = (LinkTabModel) model.get("curiTab");
        assertThat(curiTab.linkRelation.rel).isEqualTo("curies");
        assertThat(curiTab.links).hasSize(1);
        assertThat(curiTab.links.get(0).href).isEqualTo("http://example.com/{rel}");
        assertThat(curiTab.links.get(0).name).isEqualTo("ex");
        assertThat(curiTab.links.get(0).templated).isTrue();
    }

}