package de.otto.edison.hal.odyssey.model;

public class SelfModel {
    public final LinkModel link;
    public final LinkRelation linkRelation;

    public SelfModel(final LinkModel link, final LinkRelation linkRelation) {
        this.link = link;
        this.linkRelation = linkRelation;
    }

}
