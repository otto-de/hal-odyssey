package de.otto.edison.hal.odyssey.service;

import org.springframework.http.MediaType;

public class MediaTypes {

    public static final String APPLICATION_ANY_JSON_VALUE = "application/*+json";
    public static final MediaType APPLICATION_ANY_JSON = MediaType.valueOf(APPLICATION_ANY_JSON_VALUE);

    public static final String APPLICATION_HAL_JSON_VALUE = "application/hal+json";
    public static final MediaType APPLICATION_HAL_JSON = MediaType.valueOf(APPLICATION_HAL_JSON_VALUE);

    public static final String APPLICATION_HAL_JSON_UTF8_VALUE = APPLICATION_HAL_JSON_VALUE + ";charset=UTF-8";
    public static final MediaType APPLICATION_HAL_JSON_UTF8 = MediaType.valueOf(APPLICATION_HAL_JSON_UTF8_VALUE);

}
