package de.otto.edison.hal.odyssey.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrettyPrinter {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String prettyPrintJson(final String json) {
        try {
            final JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            return prettyPrintJson(jsonNode);
        } catch (Exception e) {
            return json;
        }
    }

    public static String prettyPrintJson(final JsonNode json) {
        try {
            return OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(OBJECT_MAPPER.readValue(json.toString(), Object.class));
        } catch (Exception e) {
            return json.toString();
        }
    }

}
