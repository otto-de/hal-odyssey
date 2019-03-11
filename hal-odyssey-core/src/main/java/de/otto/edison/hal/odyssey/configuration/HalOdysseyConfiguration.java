package de.otto.edison.hal.odyssey.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.odyssey.model.LinkRelationService;
import de.otto.edison.hal.odyssey.model.ModelFactory;
import de.otto.edison.hal.odyssey.service.HalClient;
import de.otto.edison.hal.odyssey.service.SimpleHalClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HalOdysseyConfiguration {

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @ConditionalOnMissingBean(LinkRelationService.class)
    @Bean
    public LinkRelationService linkRelationService() {
        return new LinkRelationService();
    }

    @ConditionalOnMissingBean(ModelFactory.class)
    @Bean
    public ModelFactory modelFactory(final LinkRelationService linkRelationService,
                                     final ObjectMapper objectMapper) {
        return new ModelFactory(linkRelationService, objectMapper);
    }

    /**
     * Builds a ClientHttpRequestFactory that is using a
     * {@link org.apache.http.impl.client.LaxRedirectStrategy lax RedirectStrategy}
     * to automatically follow redirects for all HEAD, GET, POST, and DELETE requests.
     *
     * @return ClientHttpRequestFactory
     */
    @ConditionalOnMissingBean(RestTemplate.class)
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        final CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }

    /**
     * Builds a RestTemplate that is using a configurable ClientHttpRequestFactory to build http requests.
     *
     * @param clientHttpRequestFactory the factory
     * @param restTemplateBuilder the Spring RestTemplateBuilder used to build RestTemplate instances.
     * @return RestTemplate
     */
    @ConditionalOnMissingBean(RestTemplate.class)
    @Bean
    public RestTemplate restTemplate(final ClientHttpRequestFactory clientHttpRequestFactory,
                                     final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(() -> clientHttpRequestFactory)
                .build();
    }

    @ConditionalOnMissingBean(HalClient.class)
    @Bean
    public HalClient halClient(final RestTemplate restTemplate) {
        return new SimpleHalClient(restTemplate);
    }

}
