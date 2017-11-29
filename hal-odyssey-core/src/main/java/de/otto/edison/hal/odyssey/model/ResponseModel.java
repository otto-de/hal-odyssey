package de.otto.edison.hal.odyssey.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static de.otto.edison.hal.odyssey.model.PrettyPrinter.prettyPrintJson;

public class ResponseModel {

    public static final ResponseModel EMPTY_RESPONSE = new ResponseModel();

    public final String body;
    public final HttpHeaders headers;

    public ResponseModel() {
        this.body = "";
        this.headers = new HttpHeaders();
    }

    public ResponseModel(final ResponseEntity<String> response) throws IOException {
        headers = response.getHeaders();
        if (response.getStatusCode().is2xxSuccessful()) {
            body = prettyPrintJson(response.getBody());
        } else {
            body = response.getBody();
        }
    }
}
