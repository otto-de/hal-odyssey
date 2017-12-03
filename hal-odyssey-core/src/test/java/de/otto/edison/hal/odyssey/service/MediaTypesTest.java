package de.otto.edison.hal.odyssey.service;


import org.junit.Test;

import static de.otto.edison.hal.odyssey.service.MediaTypes.APPLICATION_ANY_JSON;
import static de.otto.edison.hal.odyssey.service.MediaTypes.APPLICATION_HAL_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class MediaTypesTest {

    @Test
    public void shouldIncludeOtherJsonTypes() {
        assertThat(APPLICATION_ANY_JSON.includes(APPLICATION_HAL_JSON)).isTrue();

        assertThat(APPLICATION_HAL_JSON.includes(APPLICATION_ANY_JSON)).isFalse();
        assertThat(APPLICATION_ANY_JSON.includes(APPLICATION_JSON)).isFalse();
    }

    @Test
    public void shouldBeCompatibleWithOtherJsonTypes() {
        assertThat(APPLICATION_ANY_JSON.isCompatibleWith(APPLICATION_HAL_JSON)).isTrue();
        assertThat(APPLICATION_HAL_JSON.isCompatibleWith(APPLICATION_ANY_JSON)).isTrue();
    }
}