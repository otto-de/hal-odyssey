package de.otto.edison.hal.odyssey.ui;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class PagerModel {
    public static final Set<String> PAGING_RELS = new HashSet<>(asList("first", "next", "prev", "previous", "last"));
    public static final PagerModel UNAVAILABLE = new PagerModel("", null, null, null, null);

    public final boolean available;
    public final LinkModel first;
    public final LinkModel last;
    public final LinkModel prev;
    public final LinkModel next;

    private PagerModel(final String self,
                       final LinkModel first,
                       final LinkModel prev,
                       final LinkModel next,
                       final LinkModel last) {
        this.first = nonSelf(first, self);
        this.prev = nonSelf(prev, self);
        this.next = nonSelf(next, self);
        this.last = nonSelf(last, self);
        this.available = first != null || last != null || prev != null || next != null;
    }

    private LinkModel nonSelf(final LinkModel linkModel, final String self) {
        if (linkModel == null || linkModel.href.equals(self)) {
            return null;
        } else {
            return linkModel;
        }
    }

    public static PagerModel toPagerModel(final HalRepresentation hal) {
        final Links links = hal.getLinks();
        return new PagerModel(
                links.getLinkBy("self").map(Link::getHref).orElse(""),
                toLinkModel(links.getLinkBy("first").orElse(null)),
                toLinkModel(links.getLinkBy("prev").orElse(null), links.getLinkBy("previous").orElse(null)),
                toLinkModel(links.getLinkBy("next").orElse(null)),
                toLinkModel(links.getLinkBy("last").orElse(null))
        );
    }

    private static LinkModel toLinkModel(final Link link, final Link... more) {
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
}
