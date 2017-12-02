package de.otto.edison.hal.odyssey.model;

import org.junit.Test;

import static de.otto.edison.hal.odyssey.model.PrettyPrinter.prettyPrintJson;
import static org.assertj.core.api.Assertions.assertThat;

public class PrettyPrinterTest {

    @Test
    public void shouldPrettyPrintJson() {
        final String json = prettyPrintJson("{\"foo\":42}");
        assertThat(json).isEqualTo(
                "{\n" +
                "  \"foo\" : 42\n" +
                "}"
        );
    }
    @Test
    public void shouldIgnoreIllegalFormat() {
        final String json = prettyPrintJson("<html><body>foo</body></html>");
        assertThat(json).isEqualTo("<html><body>foo</body></html>");
    }
}