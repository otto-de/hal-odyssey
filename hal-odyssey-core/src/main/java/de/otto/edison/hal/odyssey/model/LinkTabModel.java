package de.otto.edison.hal.odyssey.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LinkTabModel {
    public final String id;
    public final LinkRelation linkRelation;
    public final List<LinkModel> links;
    public final int index;

    public LinkTabModel(final LinkRelation linkRelation, final List<LinkModel> links) {
        this(0, linkRelation, links);
    }

    public LinkTabModel(final int index, final LinkRelation linkRelation, final List<LinkModel> links) {
        this.linkRelation = linkRelation;
        this.links = links;
        this.id = UUID.randomUUID().toString();
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkTabModel that = (LinkTabModel) o;
        return index == that.index &&
                Objects.equals(id, that.id) &&
                Objects.equals(linkRelation, that.linkRelation) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linkRelation, links, index);
    }
}
