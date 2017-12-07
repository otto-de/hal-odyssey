package de.otto.edison.hal.odyssey.service;

import org.springframework.http.ResponseEntity;

public interface HalClient {
    ResponseEntity<String> get(String href,
                               String type);
}
