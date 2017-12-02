package de.otto.edison.hal.odyssey.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.odyssey.controller.OdysseyController;
import de.otto.edison.hal.odyssey.model.LinkRelationService;
import de.otto.edison.hal.odyssey.model.ModelFactory;
import de.otto.edison.hal.odyssey.service.HttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @ConditionalOnMissingBean(HttpClient.class)
    @Bean
    public HttpClient httpClient(final RestTemplateBuilder restTemplateBuilder) {
        return new HttpClient(restTemplateBuilder);
    }

    @Bean
    public OdysseyController odysseyController(final ModelFactory modelFactory,
                                               final HttpClient httpClient) {
        return new OdysseyController(modelFactory, httpClient);
    }
}
