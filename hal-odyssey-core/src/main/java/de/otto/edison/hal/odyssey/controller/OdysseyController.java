package de.otto.edison.hal.odyssey.controller;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.CuriTemplate;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.odyssey.service.HttpClient;
import de.otto.edison.hal.odyssey.ui.LinkModel;
import de.otto.edison.hal.odyssey.ui.LinkRelationService;
import de.otto.edison.hal.odyssey.ui.LinkTabModel;
import de.otto.edison.hal.odyssey.ui.PagerModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.damnhandy.uri.template.UriTemplate.fromTemplate;
import static de.otto.edison.hal.CuriTemplate.matchingCuriTemplateFor;
import static de.otto.edison.hal.odyssey.ui.PagerModel.UNAVAILABLE;
import static de.otto.edison.hal.odyssey.ui.PagerModel.toPagerModel;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
public class OdysseyController {

    private static final HalRepresentation EMPTY_HAL_REPRESENTATION = new HalRepresentation();

    private final LinkRelationService linkRelationService;
    private final HttpClient httpClient;

    @Autowired
    public OdysseyController(final LinkRelationService linkRelationService,
                             final HttpClient httpClient) {
        this.linkRelationService = linkRelationService;
        this.httpClient = httpClient;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private static final Predicate<? super Link> NON_PAGING_LINK_PREDICATE = (Link link) -> !PagerModel.PAGING_RELS.contains(link.getRel());
    private static final Predicate<? super Link> NON_SELF_LINK_PREDICATE = (Link link) -> !"self".equals(link.getRel());
    private static final Predicate<? super Link> NON_CURIES_LINK_PREDICATE = (Link link) -> !"curies".equals(link.getRel());

    @GetMapping("/")
    public ModelAndView getResource(final @RequestParam(required = false) String url,
                                    final @RequestParam(required = false) String type) throws IOException {
        if (url != null) {
            final String responseBody;
            final ResponseEntity<String> response = httpClient.get(url, type);
            final HalRepresentation hal;
            if (response.getStatusCode().is2xxSuccessful()) {
                final JsonNode jsonNode = OBJECT_MAPPER.readTree(response.getBody());
                responseBody = prettyPrint(jsonNode);
                hal = OBJECT_MAPPER.convertValue(jsonNode, HalRepresentation.class);
            } else {
                responseBody = response.getBody();
                hal = EMPTY_HAL_REPRESENTATION;
            }
            // TODO: class BrowserModel.from(hal)
            return new ModelAndView("browser", new HashMap<String,Object>() {{
                put("currentUrl", url);
                put("self", hal
                        .getLinks()
                        .getLinkBy("self")
                        .map(link -> new HashMap<String,Object>() {{
                            put("link", new LinkModel(link));
                            put("linkRelation", linkRelationService.getLinkRelation("self"));
                        }})
                        .orElse(null));
                put("customAttributes", hal.getAttributes()
                        .entrySet()
                        .stream()
                        .collect(toMap(Entry::getKey, e -> prettyPrint(e.getValue()))));
                put("pager", toPagerModel(hal));
                put("linkTabs", toLinkTabModel(hal));
                put("curiTab", toCuriesModel(hal));
                put("response", new HashMap<String, Object>() {{
                    put("body", responseBody);
                    put("headers", response.getHeaders());
                }});
            }});
        } else {
            // TODO: class BrowserModel.empty()
            return new ModelAndView("browser", new HashMap<String,Object>() {{
                put("self", null);
                put("currentUrl", "http://");
                put("customAttributes", emptyMap());
                put("linkTabs", emptyList());
                put("curiTab", new LinkTabModel(linkRelationService.getLinkRelation("curies"), emptyList()));
                put("pager", UNAVAILABLE);
                put("response", new HashMap<String, Object>() {{
                    put("body", "");
                    put("headers", emptyMap());
                }});
            }});
        }
    }

    private LinkTabModel toCuriesModel(final HalRepresentation hal) {
        final List<Link> curies = hal.getLinks().getLinksBy("curies");
        if (!curies.isEmpty()) {
            final List<LinkModel> curiLinks = curies
                    .stream()
                    .map(LinkModel::new)
                    .collect(toList());
            return new LinkTabModel(linkRelationService.getLinkRelation("curies"), curiLinks);
        } else {
            return null;
        }
    }

    private List<LinkTabModel> toLinkTabModel(final HalRepresentation hal) {
        final List<Link> curies = hal.getLinks().getLinksBy("curies");
        final List<String> sortedRels = hal.getLinks().getRels().stream().sorted().collect(toList());
        final AtomicInteger index = new AtomicInteger(0);
        return sortedRels
                .stream()
                .map(rel -> {
                    final CuriTemplate curiTemplate = matchingCuriTemplateFor(curies, rel).orElse(null);
                    final List<LinkModel> linksForRel = hal
                            .getLinks()
                            .getLinksBy(rel)
                            .stream()
                            .filter(NON_PAGING_LINK_PREDICATE)
                            .filter(NON_SELF_LINK_PREDICATE)
                            .filter(NON_CURIES_LINK_PREDICATE)
                            .map(LinkModel::new)
                            .collect(Collectors.toList());
                    if (linksForRel.isEmpty()) {
                        return null;
                    } else {
                        return new LinkTabModel(index.getAndIncrement(), linkRelationService.getLinkRelation(rel, curiTemplate), linksForRel);
                    }
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @PostMapping("/")
    public RedirectView fetchResource(final String url,
                                      final @RequestParam Map<String,Object> params,
                                      final HttpServletRequest request) {
        final UriTemplate uriTemplate = fromTemplate(url).set(nonEmpty(params));
        final String forwardTo = request.getRequestURL() + "?url=" + uriTemplate.expand();
        return new RedirectView(forwardTo, true);
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

    public String prettyPrint(final JsonNode jsonNode) {
        try {
            final Object json = OBJECT_MAPPER.readValue(jsonNode.toString(), Object.class);
            return OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }
}