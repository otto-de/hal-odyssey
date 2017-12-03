package de.otto.edison.hal.odyssey.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.CuriTemplate;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.otto.edison.hal.CuriTemplate.matchingCuriTemplateFor;
import static de.otto.edison.hal.odyssey.model.PagerModel.UNAVAILABLE;
import static de.otto.edison.hal.odyssey.model.PrettyPrinter.prettyPrintJson;
import static de.otto.edison.hal.odyssey.model.ResponseModel.EMPTY_RESPONSE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ModelFactory {

    private static final Predicate<Link> NON_PAGING_LINK_PREDICATE = (Link link) -> !PagerModel.PAGING_RELS.contains(link.getRel());
    private static final Predicate<Link> NON_SELF_LINK_PREDICATE = (Link link) -> !"self".equals(link.getRel());
    private static final Predicate<Link> NON_CURIES_LINK_PREDICATE = (Link link) -> !"curies".equals(link.getRel());
    private static final Predicate<Link> LINK_PREDICATE =
            NON_CURIES_LINK_PREDICATE
            .and(NON_PAGING_LINK_PREDICATE)
            .and(NON_SELF_LINK_PREDICATE);

    private static final HalRepresentation EMPTY_HAL_REPRESENTATION = new HalRepresentation();

    private final LinkRelationService linkRelationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ModelFactory(final LinkRelationService linkRelationService, final ObjectMapper objectMapper) {
        this.linkRelationService = linkRelationService;
        this.objectMapper = objectMapper;
    }

    public Map<String, ?> emptyMainModel() {
        return new HashMap<String,Object>() {{
            put("currentUrl", "http://");
            put("pager", UNAVAILABLE);
            put("response", EMPTY_RESPONSE);
            put("self", null);
            put("customAttributes", emptyMap());
            put("linkTabs", emptyList());
            put("curiTab", emptyCuriesModel());
            put("embeddedTabs", emptyList());
        }};
    }

    public Map<String, Object> toMainModel(final String url,
                                           final ResponseEntity<String> response) throws IOException {
        final HalRepresentation hal = toHalRepresentation(response);
        final Map<String, Object> mainModel = toResourceModel(hal, 0, LINK_PREDICATE);
        mainModel.put("currentUrl", url);
        mainModel.put("response", toResponseModel(response));
        return mainModel;
    }

    public Map<String, Object> toErrorModel(final String url,
                                           final ResponseEntity<String> response) throws IOException {
        return new HashMap<String,Object>() {{
            put("currentUrl", url);
            put("pager", UNAVAILABLE);
            put("response", toResponseModel(response));
            put("self", null);
            put("customAttributes", emptyMap());
            put("linkTabs", emptyList());
            put("curiTab", emptyCuriesModel());
            put("embeddedTabs", emptyList());
        }};
    }

    public List<EmbeddedTabModel> toEmbeddedModel(final HalRepresentation hal) {
        final List<Link> curies = hal.getLinks().getLinksBy("curies");
        final List<EmbeddedTabModel> embeddeds = new ArrayList<>();
        int tabIndex = 0;
        for (final String rel : hal.getEmbedded().getRels()) {
            final CuriTemplate curiTemplate = matchingCuriTemplateFor(curies, rel).orElse(null);
            final LinkRelation linkRelation = linkRelationService.getLinkRelation(rel, curiTemplate);
            final AtomicInteger itemIndex = new AtomicInteger(0);
            final List<Map<String, ?>> items = hal.getEmbedded().getItemsBy(rel)
                    .stream()
                    .map(item -> toResourceModel(item, itemIndex.getAndIncrement(), (link) -> true))
                    .collect(toList());
            embeddeds.add(new EmbeddedTabModel(tabIndex++, linkRelation, items));
        }
        return embeddeds;
    }

    Map<String, Object> toResourceModel(final HalRepresentation hal, final int index, final Predicate<Link> linkPredicate) {
        return new HashMap<String,Object>() {{
            put("pager", toPagerModel(hal));
            put("self", toSelfModel(hal));
            put("customAttributes", toAttributeModel(hal));
            put("linkTabs", toLinkTabModel(hal, linkPredicate));
            put("curiTab", toCuriesModel(hal));
            put("embeddedTabs", toEmbeddedModel(hal));
            put("index", index);
        }};
    }

    HalRepresentation toHalRepresentation(final ResponseEntity<String> response) throws IOException {
        final HalRepresentation hal;
        if (response.getStatusCode().is2xxSuccessful()) {
            hal = objectMapper.readValue(response.getBody(), HalRepresentation.class);
        } else {
            hal = EMPTY_HAL_REPRESENTATION;
        }
        return hal;
    }

    SelfModel toSelfModel(final HalRepresentation hal) {
        return hal
                .getLinks()
                .getLinkBy("self")
                .map(link -> new SelfModel(new LinkModel(link), linkRelationService.getLinkRelation("self")))
                .orElse(null);
    }

    LinkTabModel emptyCuriesModel() {
        final LinkRelation curiesRel = linkRelationService.getLinkRelation("curies");
        return new LinkTabModel(curiesRel, emptyList());
    }

    LinkTabModel toCuriesModel(final HalRepresentation hal) {
        final List<Link> curies = hal != null ? hal.getLinks().getLinksBy("curies") : emptyList();
        final LinkRelation curiesRel = linkRelationService.getLinkRelation("curies");
        if (!curies.isEmpty()) {
            final List<LinkModel> curiLinks = curies
                    .stream()
                    .map(LinkModel::new)
                    .collect(toList());
            return new LinkTabModel(curiesRel, curiLinks);
        } else {
            return new LinkTabModel(curiesRel, emptyList());
        }
    }

    PagerModel toPagerModel(final HalRepresentation hal) {
        final Links links = hal.getLinks();
        return new PagerModel(
                links.getLinkBy("self").map(Link::getHref).orElse(""),
                toLinkModel(links.getLinkBy("first").orElse(null)),
                toLinkModel(links.getLinkBy("prev").orElse(null), links.getLinkBy("previous").orElse(null)),
                toLinkModel(links.getLinkBy("next").orElse(null)),
                toLinkModel(links.getLinkBy("last").orElse(null))
        );
    }

    LinkModel toLinkModel(final Link link, final Link... more) {
        if (link != null) {
            return new LinkModel(link);
        }
        if (more != null && more.length > 0) {
            for (Link otherLink : more) {
                if (otherLink != null) {
                    return new LinkModel(otherLink);
                }
            }
        }
        return null;
    }

    List<LinkTabModel> toLinkTabModel(final HalRepresentation hal, final Predicate<Link> linkPredicate) {
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
                            .filter(linkPredicate)
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

    Map<String, String> toAttributeModel(final HalRepresentation hal) {
        return hal.getAttributes()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> prettyPrintJson(e.getValue())));
    }

    ResponseModel toResponseModel(final ResponseEntity<String> response) throws IOException {
        return new ResponseModel(response);
    }
}
