package de.otto.edison.hal.odyssey.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Objects;

import static de.otto.edison.hal.odyssey.model.PrettyPrinter.prettyPrintJson;

public class ResponseModel {

    public static final ResponseModel EMPTY_RESPONSE = new ResponseModel(new HttpHeaders(), "");

    public final String body;
    public final HttpHeaders headers;

    public ResponseModel(final HttpHeaders headers, final String body) {
        this.body = body;
        this.headers = headers;
    }

    public ResponseModel(final ResponseEntity<String> response) throws IOException {
        headers = response.getHeaders();
        if (response.getStatusCode().is2xxSuccessful()) {
            body = prettyPrintJson(response.getBody());
        } else {
            body = response.getBody();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseModel that = (ResponseModel) o;
        return Objects.equals(body, that.body) &&
                Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, headers);
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "body='" + body + '\'' +
                ", headers=" + headers +
                '}';
    }
}
