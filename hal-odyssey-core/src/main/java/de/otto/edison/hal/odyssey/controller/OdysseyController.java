package de.otto.edison.hal.odyssey.controller;

import com.damnhandy.uri.template.UriTemplate;
import de.otto.edison.hal.odyssey.model.ModelFactory;
import de.otto.edison.hal.odyssey.service.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.damnhandy.uri.template.UriTemplate.fromTemplate;
import static java.net.URLEncoder.encode;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

@Controller
public class OdysseyController {

    private final ModelFactory modelFactory;
    private final HttpClient httpClient;

    @Autowired
    public OdysseyController(final ModelFactory modelFactory,
                             final HttpClient httpClient) {
        this.modelFactory = modelFactory;
        this.httpClient = httpClient;
    }

    @GetMapping("/")
    public ModelAndView getResource(final @RequestParam(required = false) String url,
                                    final @RequestParam(required = false) String type,
                                    final HttpServletRequest request) throws IOException {
        if (url != null) {
            final ResponseEntity<String> response = httpClient.get(url, type);
            return new ModelAndView("main", modelFactory.toMainModel(url, response));
        } else {
            return new ModelAndView("main", modelFactory.emptyMainModel());
        }
    }

    @PostMapping("/")
    public RedirectView fetchResource(final String url,
                                      final @RequestParam Map<String,Object> params,
                                      final HttpServletRequest request) {
        try {
            final UriTemplate uriTemplate = fromTemplate(url).set(nonEmpty(params));
            final String forwardTo = request.getRequestURL() + "?url=" + encode(uriTemplate.expand(), "UTF-8");
            return new RedirectView(forwardTo, true);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Map<String, Object> nonEmpty(final Map<String, Object> params) {
        if (params != null) {
            return params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().equals(""))
                    .collect(toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue)
                    );
        } else {
            return emptyMap();
        }

    }

}