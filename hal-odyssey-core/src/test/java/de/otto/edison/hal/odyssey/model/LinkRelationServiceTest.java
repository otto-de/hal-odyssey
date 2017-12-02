package de.otto.edison.hal.odyssey.model;

import de.otto.edison.hal.CuriTemplate;
import de.otto.edison.hal.Link;
import org.junit.Test;

import static de.otto.edison.hal.CuriTemplate.curiTemplateFor;
import static de.otto.edison.hal.Link.curi;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkRelationServiceTest {

    @Test
    public void shouldHandleUnknownRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        final LinkRelation curies = linkRelationService.getLinkRelation("foo");
        assertThat(curies.rel).isEqualTo("foo");
        assertThat(curies.href).isNull();
        assertThat(curies.description).isNull();
    }

    @Test
    public void shouldExpandCuriedRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        final CuriTemplate curiTemplate = curiTemplateFor(
                curi("ex", "http://example.com/{rel}")
        );
        final LinkRelation example = linkRelationService.getLinkRelation("ex:foo", curiTemplate);
        assertThat(example.rel).isEqualTo("ex:foo");
        assertThat(example.href).isEqualTo("http://example.com/foo");
        assertThat(example.description).isEqualTo("http://example.com/foo");
    }

    @Test
    public void shouldHandleCustomRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        final LinkRelation example = linkRelationService.getLinkRelation("http://example.com/rels/foo");
        assertThat(example.rel).isEqualTo("http://example.com/rels/foo");
        assertThat(example.href).isEqualTo("http://example.com/rels/foo");
        assertThat(example.description).isEqualTo("http://example.com/rels/foo");
    }

    @Test
    public void shouldRegisterCustomRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        linkRelationService.register(new LinkRelation("foo", "http://example.com/rels/foo", "Foo"));
        final LinkRelation example = linkRelationService.getLinkRelation("foo");
        assertThat(example.rel).isEqualTo("foo");
        assertThat(example.href).isEqualTo("http://example.com/rels/foo");
        assertThat(example.description).isEqualTo("Foo");
    }

    @Test
    public void shouldResolveRegisteredCuriedRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        linkRelationService.register(new LinkRelation("ex:foo", "http://example.com/rels/foo", "Foo"));

        final CuriTemplate curiTemplate = curiTemplateFor(
                curi("ex", "http://example.com/rels/{rel}")
        );
        LinkRelation example = linkRelationService.getLinkRelation("ex:foo", curiTemplate);
        assertThat(example.rel).isEqualTo("ex:foo");
        assertThat(example.href).isEqualTo("http://example.com/rels/foo");
        assertThat(example.description).isEqualTo("Foo");
        example = linkRelationService.getLinkRelation("http://example.com/rels/foo", curiTemplate);
        assertThat(example.rel).isEqualTo("ex:foo");
        assertThat(example.href).isEqualTo("http://example.com/rels/foo");
        assertThat(example.description).isEqualTo("Foo");
    }

    @Test
    public void shouldResolveRegisteredExpandedCuriedRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        linkRelationService.register(new LinkRelation("http://example.com/rels/foo", "http://example.com/some/foo/doc", "Foo"));

        final CuriTemplate curiTemplate = curiTemplateFor(
                curi("ex", "http://example.com/rels/{rel}")
        );
        LinkRelation example = linkRelationService.getLinkRelation("ex:foo", curiTemplate);
        assertThat(example.rel).isEqualTo("http://example.com/rels/foo");
        assertThat(example.href).isEqualTo("http://example.com/some/foo/doc");
        assertThat(example.description).isEqualTo("Foo");
        example = linkRelationService.getLinkRelation("http://example.com/rels/foo", curiTemplate);
        assertThat(example.rel).isEqualTo("http://example.com/rels/foo");
        assertThat(example.href).isEqualTo("http://example.com/some/foo/doc");
        assertThat(example.description).isEqualTo("Foo");
    }

    @Test
    public void shouldResolveCuriRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        final LinkRelation curies = linkRelationService.getLinkRelation("curies");
        assertThat(curies.rel).isEqualTo("curies");
        assertThat(curies.href).isEqualTo("https://tools.ietf.org/html/draft-kelly-json-hal-08");
        assertThat(curies.description).isEqualTo("Reserved link relation which can be used to hint at the location of resource documentation.");
    }

    @Test
    public void shouldResolveIanaRel() {
        final LinkRelationService linkRelationService = new LinkRelationService();
        final LinkRelation curies = linkRelationService.getLinkRelation("item");
        assertThat(curies.rel).isEqualTo("item");
        assertThat(curies.href).isEqualTo("https://www.iana.org/assignments/link-relations/link-relations.xhtml");
        assertThat(curies.description).isEqualTo("The target IRI points to a resource that is a member of the collection represented by the context IRI.");
    }
}