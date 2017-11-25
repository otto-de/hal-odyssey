package de.otto.edison.hal.browser.controller;

import java.util.List;

public class LinkTabModel {
    public final String rel;
    public final String relHref;
    public final String relDesc;
    public final List<LinkModel> links;

    public LinkTabModel(String rel, String relHref, String relDesc, List<LinkModel> links) {
        this.rel = rel;
        this.relHref = relHref;
        this.relDesc = relDesc;
        this.links = links;
    }
}
