package de.otto.edison.hal.odyssey.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmbeddedTabModel {
    public final String id;
    public final LinkRelation linkRelation;
    public final List<Map<String,?>> items;
    public final int index;

    public EmbeddedTabModel(final int index, final LinkRelation linkRelation, final List<Map<String, ?>> items) {
        this.id = UUID.randomUUID().toString();
        this.index = index;
        this.linkRelation = linkRelation;
        this.items = items;
    }
}
