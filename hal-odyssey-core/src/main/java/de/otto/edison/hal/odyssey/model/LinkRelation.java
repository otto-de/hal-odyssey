package de.otto.edison.hal.odyssey.model;

public class LinkRelation {
    public final String rel;
    public final String href;
    public final String description;

    public LinkRelation(final String rel, final String href, final String description) {
        this.rel = rel;
        this.href = href;
        this.description = description;
    }

}
