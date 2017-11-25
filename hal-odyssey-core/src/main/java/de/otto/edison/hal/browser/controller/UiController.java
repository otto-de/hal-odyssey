package de.otto.edison.hal.browser.controller;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.CuriTemplate;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.traverson.LinkResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.damnhandy.uri.template.UriTemplate.fromTemplate;
import static de.otto.edison.hal.CuriTemplate.matchingCuriTemplateFor;
import static de.otto.edison.hal.traverson.Traverson.traverson;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
public class UiController {

    private static final HalRepresentation EMPTY_HAL_REPRESENTATION = new HalRepresentation();

    private RestTemplate restTemplate = new RestTemplate();

    private final LinkResolver fakeLinkResolver = new LinkResolver() {
        @Override
        public String apply(final Link link) throws IOException {
            if (link.getHref().startsWith("http://localhost:8080")) {
                return "{" +
                        "   \"foo\":\"bar\"," +
                        "   \"foobar\":{\"one\":\"eins\",\"two\":\"zwei\"}," +
                        "   \"barfoo\":[{\"one\":\"eins\"},{\"two\":\"zwei\"}]," +
                        "   \"_links\":{" +
                        "       \"curies\": [" +
                        "           {\"name\":\"example\", \"href\":\"http://localhost:8080/rels/{rel}\", \"templated\": true}" +
                        "       ]," +
                        "       \"self\": {" +
                        "           \"href\":\"" + link.getHref() + "\", \"title\":\"" + link.getTitle() + "\", \"type\":\"" + link.getType() + "\"" +
                        "       }," +
                        "       \"http://localhost:8080/rels/foo\": [" +
                        "           {\"href\":\"http://localhost:8080/foo/42\", \"title\":\"Foo 42\", \"type\":\"application/hal+json\", \"profile\":\"http://localhost:8080/profiles/test\"}," +
                        "           {\"href\":\"http://localhost:8080/foo/43\", \"title\":\"Foo 43\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/foo/44\", \"title\":\"Foo 44\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/foo/45\", \"title\":\"Foo 45\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/foo/46\", \"title\":\"Foo 46\", \"type\":\"application/hal+json\"}" +
                        "       ]," +
                        "       \"http://localhost:8080/link-relations/bar\": [" +
                        "           {\"href\":\"http://localhost:8080/bar/42\", \"title\":\"Bar 42\", \"type\":\"application/hal+json\", \"profile\":\"http://localhost:8080/profiles/test\"}," +
                        "           {\"href\":\"http://localhost:8080/bar/43\", \"title\":\"Bar 43\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/bar{/id}{?x}{&y}\", \"templated\": true, \"title\":\"Bar 44\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/bar/45\", \"title\":\"Bar 45\", \"type\":\"application/hal+json\"}," +
                        "           {\"href\":\"http://localhost:8080/bar/46\", \"title\":\"Bar 46\", \"type\":\"application/hal+json\"}" +
                        "       ]" +
                        "   }" +
                        "}";
            } else {
                return restTemplate.getForObject(URI.create(link.getHref()), String.class);
            }
        }
    };
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @GetMapping("/")
    public ModelAndView getResource(final @RequestParam(required = false) String url) throws IOException {
        if (url != null) {
            final HalRepresentation hal = traverson(fakeLinkResolver)
                    .startWith(url)
                    .getResource()
                    .orElse(EMPTY_HAL_REPRESENTATION);

            return new ModelAndView("browser", new HashMap<String,Object>() {{
                put("currentUrl", url);
                put("customAttributes", hal.getAttributes()
                        .entrySet()
                        .stream()
                        .collect(toMap(Entry::getKey, e -> prettyPrint(e.getValue()))));
                put("links", toLinkModel(hal));
            }});
        } else {
            return new ModelAndView("browser", new HashMap<String,Object>() {{
                put("currentUrl", "http://");
                put("customAttributes", emptyMap());
                put("links", emptyList());
            }});
        }
    }

    private List<LinkModel> toLinkModel(final HalRepresentation hal) {
        return hal
                .getLinks()
                .stream()
                .map(link -> {
                    final List<Link> curies = hal.getLinks().getLinksBy("curies");
                    final Optional<CuriTemplate> curiTemplate = matchingCuriTemplateFor(curies, link.getRel());
                    return new LinkModel(link, curiTemplate);
                })
                .collect(toList());
    }

    @PostMapping("/")
    public RedirectView fetchResource(final String url,
                                      final @RequestParam Map<String,Object> params,
                                      final HttpServletRequest request) {
        final UriTemplate uriTemplate = fromTemplate(url).set(nonEmpty(params));
        final String forwardTo = request.getRequestURL() + "?url=" + uriTemplate.expand();
        return new RedirectView(forwardTo, true);
    }

    private Map<String, Object> nonEmpty(final Map<String, Object> params) {
        if (params != null) {
            return params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().equals(""))
                    .collect(toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue)
                    );
        } else {
            return emptyMap();
        }

    }

    public String prettyPrint(final JsonNode jsonNode) {
        try {
            final Object json = OBJECT_MAPPER.readValue(jsonNode.toString(), Object.class);
            return OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }
}