package de.otto.edison.hal.odyssey.model;

import java.util.Objects;

public class SelfModel {
    public final LinkModel link;
    public final LinkRelation linkRelation;

    public SelfModel(final LinkModel link, final LinkRelation linkRelation) {
        this.link = link;
        this.linkRelation = linkRelation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelfModel selfModel = (SelfModel) o;
        return Objects.equals(link, selfModel.link) &&
                Objects.equals(linkRelation, selfModel.linkRelation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, linkRelation);
    }
}
