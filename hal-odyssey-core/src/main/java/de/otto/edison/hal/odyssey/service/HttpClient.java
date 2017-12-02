package de.otto.edison.hal.odyssey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.valueOf;

public class HttpClient {

    private static final MediaType APPLICATION_HAL_JSON = valueOf("application/hal+json");
    private final RestTemplate restTemplate;

    public HttpClient(final RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<String> get(final String href,
                                      final String type) {
        return restTemplate.exchange(
                href,
                HttpMethod.GET,
                new HttpEntity<String>(headersFor(type)),
                String.class);
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
