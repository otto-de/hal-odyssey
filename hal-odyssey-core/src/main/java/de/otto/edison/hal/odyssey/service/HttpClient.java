package de.otto.edison.hal.odyssey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.valueOf;

@Service
public class HttpClient {

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

    private static final MediaType APPLICATION_HAL_JSON = valueOf("application/hal+json");
    private final RestTemplate restTemplate;

    @Autowired
    HttpClient(final RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<String> get(final String href,
                                      final String type) {
        if (href.startsWith("http://localhost:8080/example")) {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.CONTENT_TYPE, asList("application/hal+json"));
            headers.put(HttpHeaders.WARNING, asList("Example Content", "Served by Fake Service"));
            return new ResponseEntity<>(format(EXAMPLE_JSON, href), headers, OK);
        } else {
            return restTemplate.exchange(
                    href,
                    HttpMethod.GET,
                    new HttpEntity<String>(headersFor(type)),
                    String.class);
        }
    }

    private HttpHeaders headersFor(final String type) {
        final HttpHeaders headers = new HttpHeaders();
        if (type == null || type.isEmpty()) {
            headers.setAccept(asList(
                    APPLICATION_HAL_JSON,
                    APPLICATION_JSON)
            );
        } else {
            headers.setAccept(singletonList(valueOf(type)));
        }
        return headers;
    }

}
