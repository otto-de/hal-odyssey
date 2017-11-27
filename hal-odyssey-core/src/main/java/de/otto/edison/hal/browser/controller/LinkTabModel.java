package de.otto.edison.hal.browser.controller;

import java.util.List;
import java.util.UUID;

public class LinkTabModel {
    public final String id;
    public final String rel;
    public final String relHref;
    public final String relDesc;
    public final List<LinkModel> links;
    public final int index;

    public LinkTabModel(int index, String rel, String relHref, String relDesc, List<LinkModel> links) {
        this.rel = rel;
        this.relHref = relHref;
        this.relDesc = relDesc;
        this.links = links;
        this.id = UUID.randomUUID().toString();
        this.index = index;
    }
}
