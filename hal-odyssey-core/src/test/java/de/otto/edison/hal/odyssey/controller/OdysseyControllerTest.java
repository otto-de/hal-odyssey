package de.otto.edison.hal.odyssey.controller;

import de.otto.edison.hal.odyssey.configuration.HalOdysseyConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {TestServer.class, HalOdysseyConfiguration.class})
public class OdysseyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldGetMainPage() throws Exception {
        mvc.perform(get("/").accept(TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>HAL Odyssey</title>")));
    }
}
