package de.otto.edison.hal.odyssey.service;

import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;

public class HttpClientTest {

    @Test
    public void shouldGetResourceAsJson() {
        // given
        final RestTemplate mockTemplate = mock(RestTemplate.class);
        final RestTemplateBuilder mockTemplateBuilder = mock(RestTemplateBuilder.class);
        when(mockTemplateBuilder.build()).thenReturn(mockTemplate);
        final HttpClient httpClient = new HttpClient(mockTemplateBuilder);

        // when
        httpClient.get("http://example.com", null);

        // then
        final HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setAccept(asList(
                MediaType.valueOf("application/hal+json"),
                APPLICATION_JSON)
        );

        verify(mockTemplate).exchange(
                "http://example.com",
                HttpMethod.GET,
                new HttpEntity<String>(expectedHeaders),
                String.class);
    }

    @Test
    public void shouldGetResourceAsHtml() {
        // given
        final RestTemplate mockTemplate = mock(RestTemplate.class);
        final RestTemplateBuilder mockTemplateBuilder = mock(RestTemplateBuilder.class);
        when(mockTemplateBuilder.build()).thenReturn(mockTemplate);
        final HttpClient httpClient = new HttpClient(mockTemplateBuilder);

        // when
        httpClient.get("http://example.com", "text/html");

        // then
        final HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setAccept(singletonList(TEXT_HTML));

        verify(mockTemplate).exchange(
                "http://example.com",
                HttpMethod.GET,
                new HttpEntity<String>(expectedHeaders),
                String.class);
    }
}