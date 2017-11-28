package de.otto.edison.hal.odyssey.service;

import de.otto.edison.hal.Link;
import de.otto.edison.hal.traverson.LinkResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static java.lang.String.format;

@Service
public class OdysseyLinkResolver implements LinkResolver {

    private static final String EXAMPLE_JSON = "{" +
            "   \"foo\":\"bar\"," +
            "   \"foobar\":{\"one\":\"eins\",\"two\":\"zwei\"}," +
            "   \"barfoo\":[{\"one\":\"eins\"},{\"two\":\"zwei\"}]," +
            "   \"_links\":{" +
            "       \"curies\": [" +
            "           {\"name\":\"example\", \"href\":\"http://example.com/rels/{rel}\", \"templated\": true}," +
            "           {\"name\":\"otto\", \"href\":\"http://spec.otto.de/rels/{rel}\", \"templated\": true}" +
            "       ]," +
            "       \"self\": {" +
            "           \"href\":\"%s\", \"title\":\"Some Resource\", \"type\":\"application/hal+json\", \"profile\":\"http://example.com/profiles/example\"" +
            "       }," +
            "       \"first\": {" +
            "           \"href\":\"http://localhost:8080/example?page=0\"" +
            "       }," +
            "       \"next\": {" +
            "           \"href\":\"http://localhost:8080/example?page=5\"" +
            "       }," +
            "       \"prev\": {" +
            "           \"href\":\"http://localhost:8080/example?page=3\"" +
            "       }," +
            "       \"last\": {" +
            "           \"href\":\"http://localhost:8080/example?page=42\"" +
            "       }," +
            "       \"example:foo\": [" +
            "           {\"href\":\"http://localhost:8080/example/foo/42\", \"title\":\"Foo 42\", \"type\":\"application/hal+json\", \"profile\":\"http://localhost:8080/profiles/test/test/test;version=1\"}," +
            "           {\"href\":\"http://localhost:8080/example/foo/43\", \"title\":\"Foo 43\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/foo/44\", \"title\":\"Foo 44\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/foo/45\", \"title\":\"Foo 45\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/foo/46\", \"title\":\"Foo 46\", \"type\":\"application/hal+json\"}" +
            "       ]," +
            "       \"bar\": [" +
            "           {\"href\":\"http://localhost:8080/example/bar/42\", \"title\":\"Bar 42\", \"type\":\"application/hal+json\", \"profile\":\"http://localhost:8080/profiles/test\"}," +
            "           {\"href\":\"http://localhost:8080/example/bar/43\", \"title\":\"Bar 43\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/bar{/id}{?x}{&y}\", \"templated\": true, \"title\":\"Bar 44\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/bar/45\", \"title\":\"Bar 45\", \"type\":\"application/hal+json\"}," +
            "           {\"href\":\"http://localhost:8080/example/bar/46\", \"title\":\"Bar 46\", \"type\":\"application/hal+json\"}" +
            "       ]" +
            "   }" +
            "}";

    private final RestTemplate restTemplate;

    @Autowired
    OdysseyLinkResolver(final RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    /**
     * Resolves an absolute link and returns the linked resource representation as a String.
     *
     * @param link the link of the resource
     * @return String containing the {@code application/hal+json} representation of the resource
     * @throws IOException if a low-level I/O problem (unexpected end-of-input, network error) occurs.
     */
    @Override
    public String apply(Link link) throws IOException {
        if (link.getHref().startsWith("http://localhost:8080/example")) {
            return format(EXAMPLE_JSON, link.getHref());
        } else {
            return restTemplate.getForObject(URI.create(link.getHref()), String.class);
        }
    }

}
