package de.otto.edison.hal.odyssey.model;

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

    public PagerModel(final String self,
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

}
