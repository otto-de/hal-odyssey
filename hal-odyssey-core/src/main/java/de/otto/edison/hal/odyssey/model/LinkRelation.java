package de.otto.edison.hal.odyssey.model;

import java.util.Objects;

public class LinkRelation {
    public final String rel;
    public final String href;
    public final String description;

    public LinkRelation(final String rel, final String href, final String description) {
        this.rel = rel;
        this.href = href;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkRelation that = (LinkRelation) o;
        return Objects.equals(rel, that.rel) &&
                Objects.equals(href, that.href) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, href, description);
    }
}
