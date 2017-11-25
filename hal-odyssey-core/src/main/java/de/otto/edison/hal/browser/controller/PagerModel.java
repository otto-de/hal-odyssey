package de.otto.edison.hal.browser.controller;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;

public class PagerModel {
    public static final Set<String> PAGING_RELS = new HashSet<>(asList("first", "next", "prev", "previous", "last"));
    public static final PagerModel UNAVAILABLE = new PagerModel(null, null, null, null);

    public final boolean available;
    public final LinkModel first;
    public final LinkModel last;
    public final LinkModel prev;
    public final LinkModel next;

    private PagerModel(final LinkModel first,
                       final LinkModel prev,
                       final LinkModel next,
                       final LinkModel last) {
        this.first = first;
        this.last = last;
        this.prev = prev;
        this.next = next;
        this.available = first != null || last != null || prev != null || next != null;
    }

    public static PagerModel toPagerModel(final HalRepresentation hal) {
        final Links links = hal.getLinks();
        return new PagerModel(
                toLinkModel(links.getLinkBy("first").orElse(null)),
                toLinkModel(links.getLinkBy("prev").orElse(null), links.getLinkBy("previous").orElse(null)),
                toLinkModel(links.getLinkBy("next").orElse(null)),
                toLinkModel(links.getLinkBy("last").orElse(null))
        );
    }

    private static LinkModel toLinkModel(final Link link, final Link... more) {
        if (link != null) {
            return new LinkModel(link, empty());
        }
        if (more != null && more.length > 0) {
            for (Link otherLink : more) {
                if (otherLink != null) {
                    return new LinkModel(otherLink, empty());
                }
            }
        }
        return null;
    }
}
