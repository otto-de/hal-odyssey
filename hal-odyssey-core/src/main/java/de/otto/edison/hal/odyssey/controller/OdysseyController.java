package de.otto.edison.hal.odyssey.controller;

import com.damnhandy.uri.template.UriTemplate;
import de.otto.edison.hal.odyssey.model.ModelFactory;
import de.otto.edison.hal.odyssey.service.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.damnhandy.uri.template.UriTemplate.fromTemplate;
import static de.otto.edison.hal.odyssey.service.MediaTypes.APPLICATION_ANY_JSON;
import static java.net.URLEncoder.encode;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Controller
public class OdysseyController {

    private final ModelFactory modelFactory;
    private final HttpClient httpClient;

    public OdysseyController(final ModelFactory modelFactory,
                             final HttpClient httpClient) {
        this.modelFactory = modelFactory;
        this.httpClient = httpClient;
    }

    @GetMapping("/")
    public ModelAndView getResource(final @RequestParam(required = false) String url,
                                    final @RequestParam(required = false) String type) throws IOException {
        if (url == null) {
            return new ModelAndView("index", modelFactory.emptyMainModel());
        }
        if (type != null && !type.startsWith("application/json") && !type.startsWith("application/hal+json")) {
            return new ModelAndView("redirect:" + url);
        }

        try {
            final ResponseEntity<String> response = httpClient.get(url, type);
            if (response.getStatusCode().is2xxSuccessful()) {
                final MediaType contentType = response.getHeaders().getContentType();
                if (contentType == null || contentType.isCompatibleWith(APPLICATION_JSON) || contentType.isCompatibleWith(APPLICATION_ANY_JSON)) {
                    return new ModelAndView("main", modelFactory.toMainModel(url, response));
                } else {
                    return new ModelAndView("redirect:" + url);
                }
            }
            if (response.getStatusCode().is3xxRedirection()) {
                return new ModelAndView("redirect:" + url);
            }
        } catch (final HttpStatusCodeException e) {
            return new ModelAndView("index", modelFactory.toErrorModel(url, e));
        } catch (final ResourceAccessException e) {
            return new ModelAndView("index", modelFactory.toErrorModel(url, e));
        }
        throw new IllegalStateException("Unable to process '" + url + "'");
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