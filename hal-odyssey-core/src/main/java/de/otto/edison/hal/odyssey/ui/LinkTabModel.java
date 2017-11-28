package de.otto.edison.hal.odyssey.ui;

import java.util.List;
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
}
