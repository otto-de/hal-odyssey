package de.otto.edison.hal.odyssey.ui;

import de.otto.edison.hal.CuriTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

@Service
public class LinkRelationService {

    public static final String IANA_RELS_HREF = "https://www.iana.org/assignments/link-relations/link-relations.xhtml";
    public static final String HAL_JSON_HREF = "https://tools.ietf.org/html/draft-kelly-json-hal-08";

    public static final List<LinkRelation> DEFAULT_RELS = asList(
            new LinkRelation("curies",              HAL_JSON_HREF, "Reserved link relation which can be used to hint at the location of resource documentation."),
            new LinkRelation("about",               IANA_RELS_HREF, "Refers to a resource that is the subject of the link's context."),
            new LinkRelation("alternate",           IANA_RELS_HREF, "Refers to a substitute for this context"),
            new LinkRelation("appendix",            IANA_RELS_HREF, "Refers to an appendix."),
            new LinkRelation("archives",            IANA_RELS_HREF, "Refers to a collection of records, documents, or other materials of historical interest."),
            new LinkRelation("author",              IANA_RELS_HREF, "Refers to the context's author."),
            new LinkRelation("blocked-by",          IANA_RELS_HREF, "Identifies the entity that blocks access to a resource following receipt of a legal demand."),
            new LinkRelation("bookmark",            IANA_RELS_HREF, "Gives a permanent link to use for bookmarking purposes."),
            new LinkRelation("canonical",           IANA_RELS_HREF, "Designates the preferred version of a resource (the IRI and its contents)."),
            new LinkRelation("chapter",             IANA_RELS_HREF, "Refers to a chapter in a collection of resources."),
            new LinkRelation("cite-as",             IANA_RELS_HREF, "Indicates that the link target is preferred over the link context for the purpose of referencing."),
            new LinkRelation("collection",          IANA_RELS_HREF, "The target IRI points to a resource which represents the collection resource for the context IRI."),
            new LinkRelation("contents",            IANA_RELS_HREF, "Refers to a table of contents."),
            new LinkRelation("convertedFrom",       IANA_RELS_HREF, "The document linked to was later converted to the document that contains this link relation. For example, an RFC can have a link to the Internet-Draft that became the RFC; in that case, the link relation would be \"convertedFrom\"."),
            new LinkRelation("copyright",           IANA_RELS_HREF, "Refers to a copyright statement that applies to the link's context."),
            new LinkRelation("create-form",         IANA_RELS_HREF, "The target IRI points to a resource where a submission form can be obtained."),
            new LinkRelation("current",             IANA_RELS_HREF, "Refers to a resource containing the most recent item(s) in a collection of resources."),
            new LinkRelation("desribedby",          IANA_RELS_HREF, "Refers to a resource providing information about the link's context."),
            new LinkRelation("describes",           IANA_RELS_HREF, "The relationship A 'describes' B asserts that resource A provides a description of resource B. There are no constraints on the format or representation of either A or B, neither are there any further constraints on either resource."),
            new LinkRelation("disclosure",          IANA_RELS_HREF, "Refers to a list of patent disclosures made with respect to material for which 'disclosure' relation is specified."),
            new LinkRelation("dns-prefetch",        IANA_RELS_HREF, "Used to indicate an origin that will be used to fetch required resources for the link context, and that the user agent ought to resolve as early as possible."),
            new LinkRelation("duplicate",           IANA_RELS_HREF, "Refers to a resource whose available representations are byte-for-byte identical with the corresponding representations of the context IRI."),
            new LinkRelation("edit",                IANA_RELS_HREF, "Refers to a resource that can be used to edit the link's context."),
            new LinkRelation("edit-form",           IANA_RELS_HREF, "The target IRI points to a resource where a submission form for editing associated resource can be obtained."),
            new LinkRelation("edit-media",          IANA_RELS_HREF, "Refers to a resource that can be used to edit media associated with the link's context."),
            new LinkRelation("enclosure",           IANA_RELS_HREF, "Identifies a related resource that is potentially large and might require special handling."),
            new LinkRelation("first",               IANA_RELS_HREF, "An IRI that refers to the furthest preceding resource in a series of resources."),
            new LinkRelation("glossary",            IANA_RELS_HREF, "Refers to a glossary of terms."),
            new LinkRelation("help",                IANA_RELS_HREF, "Refers to context-sensitive help."),
            new LinkRelation("hosts",               IANA_RELS_HREF, "Refers to a resource hosted by the server indicated by the link context."),
            new LinkRelation("hub",                 IANA_RELS_HREF, "Refers to a hub that enables registration for notification of updates to the context."),
            new LinkRelation("icon",                IANA_RELS_HREF, "Refers to an icon representing the link's context."),
            new LinkRelation("index",               IANA_RELS_HREF, "Refers to an index."),
            new LinkRelation("item",                IANA_RELS_HREF, "The target IRI points to a resource that is a member of the collection represented by the context IRI."),
            new LinkRelation("last",                IANA_RELS_HREF, "An IRI that refers to the furthest following resource in a series of resources."),
            new LinkRelation("lastest-version",     IANA_RELS_HREF, "Points to a resource containing the latest (e.g., current) version of the context."),
            new LinkRelation("license",             IANA_RELS_HREF, "Refers to a license associated with this context."),
            new LinkRelation("lrdd",                IANA_RELS_HREF, "Refers to further information about the link's context, expressed as a LRDD (\"Link-based Resource Descriptor Document\") resource. See [RFC6415] for information about processing this relation type in host-meta documents. When used elsewhere, it refers to additional links and other metadata. Multiple instances indicate additional LRDD resources. LRDD resources MUST have an \"application/xrd+xml\" representation, and MAY have others."),
            new LinkRelation("memento",             IANA_RELS_HREF, "The Target IRI points to a Memento, a fixed resource that will not change state anymore."),
            new LinkRelation("monitor",             IANA_RELS_HREF, "Refers to a resource that can be used to monitor changes in an HTTP resource."),
            new LinkRelation("monitor-group",       IANA_RELS_HREF, "Refers to a resource that can be used to monitor changes in a specified group of HTTP resources."),
            new LinkRelation("next",                IANA_RELS_HREF, "Indicates that the link's context is a part of a series, and that the next in the series is the link target."),
            new LinkRelation("next-archive",        IANA_RELS_HREF, "Refers to the immediately following archive resource."),
            new LinkRelation("nofollow",            IANA_RELS_HREF, "Indicates that the context’s original author or publisher does not endorse the link target."),
            new LinkRelation("noreferrer",          IANA_RELS_HREF, "Indicates that no referrer information is to be leaked when following the link."),
            new LinkRelation("original",            IANA_RELS_HREF, "The Target IRI points to an Original Resource."),
            new LinkRelation("payment",             IANA_RELS_HREF, "Indicates a resource where payment is accepted."),
            new LinkRelation("pingback",            IANA_RELS_HREF, "Gives the address of the pingback resource for the link context."),
            new LinkRelation("preconnect",          IANA_RELS_HREF, "Used to indicate an origin that will be used to fetch required resources for the link context. Initiating an early connection, which includes the DNS lookup, TCP handshake, and optional TLS negotiation, allows the user agent to mask the high latency costs of establishing a connection."),
            new LinkRelation("predecessor-version", IANA_RELS_HREF, "Points to a resource containing the predecessor version in the version history."),
            new LinkRelation("prefetch",            IANA_RELS_HREF, "The prefetch link relation type is used to identify a resource that might be required by the next navigation from the link context, and that the user agent ought to fetch, such that the user agent can deliver a faster response once the resource is requested in the future."),
            new LinkRelation("preload",             IANA_RELS_HREF, "Refers to a resource that should be loaded early in the processing of the link's context, without blocking rendering."),
            new LinkRelation("prerender",           IANA_RELS_HREF, "Used to identify a resource that might be required by the next navigation from the link context, and that the user agent ought to fetch and execute, such that the user agent can deliver a faster response once the resource is requested in the future."),
            new LinkRelation("prev",                IANA_RELS_HREF, "Indicates that the link's context is a part of a series, and that the previous in the series is the link target."),
            new LinkRelation("preview",             IANA_RELS_HREF, "Refers to a resource that provides a preview of the link's context."),
            new LinkRelation("previous",            IANA_RELS_HREF, "Refers to the previous resource in an ordered series of resources. Synonym for \"prev\"."),
            new LinkRelation("prev-archive",        IANA_RELS_HREF, "Refers to the immediately preceding archive resource."),
            new LinkRelation("privace-policy",      IANA_RELS_HREF, "Refers to a privacy policy associated with the link's context."),
            new LinkRelation("profile",             IANA_RELS_HREF, "Identifying that a resource representation conforms to a certain profile, without affecting the non-profile semantics of the resource representation."),
            new LinkRelation("related",             IANA_RELS_HREF, "Identifies a related resource."),
            new LinkRelation("restconf",            IANA_RELS_HREF, "Identifies the root of RESTCONF API as configured on this HTTP server. The \"restconf\" relation defines the root of the API defined in RFC8040. Subsequent revisions of RESTCONF will use alternate relation values to support protocol versioning."),
            new LinkRelation("replies",             IANA_RELS_HREF, "Identifies a resource that is a reply to the context of the link."),
            new LinkRelation("search",              IANA_RELS_HREF, "Refers to a resource that can be used to search through the link's context and related resources."),
            new LinkRelation("section",             IANA_RELS_HREF, "Refers to a section in a collection of resources."),
            new LinkRelation("self",                IANA_RELS_HREF, "Conveys an identifier for the link's context."),
            new LinkRelation("service",             IANA_RELS_HREF, "Indicates a URI that can be used to retrieve a service document."),
            new LinkRelation("start",               IANA_RELS_HREF, "Refers to the first resource in a collection of resources."),
            new LinkRelation("stylesheet",          IANA_RELS_HREF, "Refers to a stylesheet."),
            new LinkRelation("subsection",          IANA_RELS_HREF, "Refers to a resource serving as a subsection in a collection of resources."),
            new LinkRelation("successor-version",   IANA_RELS_HREF, "Points to a resource containing the successor version in the version history."),
            new LinkRelation("tag",                 IANA_RELS_HREF, "Gives a tag (identified by the given address) that applies to the current document."),
            new LinkRelation("terms-of-service",    IANA_RELS_HREF, "Refers to the terms of service associated with the link's context."),
            new LinkRelation("timegate",            IANA_RELS_HREF, "The Target IRI points to a TimeGate for an Original Resource."),
            new LinkRelation("timemap",             IANA_RELS_HREF, "The Target IRI points to a TimeMap for an Original Resource."),
            new LinkRelation("type",                IANA_RELS_HREF, "Refers to a resource identifying the abstract semantic type of which the link's context is considered to be an instance."),
            new LinkRelation("up",                  IANA_RELS_HREF, "Refers to a parent document in a hierarchy of documents."),
            new LinkRelation("version-history",     IANA_RELS_HREF, "Points to a resource containing the version history for the context."),
            new LinkRelation("via",                 IANA_RELS_HREF, "Identifies a resource that is the source of the information in the link's context."),
            new LinkRelation("webmention",          IANA_RELS_HREF, "Identifies a target URI that supports the Webmention protcol. This allows clients that mention a resource in some form of publishing process to contact that endpoint and inform it that this resource has been mentioned."),
            new LinkRelation("working-copy",        IANA_RELS_HREF, "Points to a working copy for this resource."),
            new LinkRelation("working-copy-of",     IANA_RELS_HREF, "Points to the versioned resource from which this working copy was obtained.")
    );

    private final Map<String, LinkRelation> linkRelations = new ConcurrentHashMap<>();

    public LinkRelationService() {
        DEFAULT_RELS.forEach(linkRelation -> linkRelations.put(linkRelation.rel, linkRelation));
    }

    public void register(final LinkRelation linkRelation) {
        linkRelations.put(linkRelation.rel, linkRelation);
    }

    public void unregister(final String rel) {
        linkRelations.remove(rel);
    }

    public LinkRelation getLinkRelation(final String rel) {
        return getLinkRelation(rel, null);
    }

    /**
     * Returns a registered LinkRelation, or builds an instance using {@code rel} and {@code curiTemplate}.
     *
     * @param rel the link-relation
     * @param curiTemplate a CuriTemplate that can be used to expand the rel, or null if no matching CURI is available.
     * @return LinkRelation
     */
    public LinkRelation getLinkRelation(final String rel,
                                        final CuriTemplate curiTemplate) {
        final String curied = curiTemplate != null
                ? curiTemplate.curiedRelFrom(rel)
                : rel;
        final String expanded = curiTemplate != null
                ? curiTemplate.expandedRelFrom(rel)
                : rel;

        if (linkRelations.containsKey(curied)) {
            return linkRelations.get(rel);
        } if (linkRelations.containsKey(expanded)) {
            return linkRelations.get(expanded);
        } else{
            final String href = relHref(rel, curiTemplate);
            return new LinkRelation(rel, href, href);
        }
    }

    private String relHref(final String rel,
                           final CuriTemplate curiTemplate) {
        if (rel.startsWith("http://") || rel.startsWith("https://")) {
            return rel;
        }
        if (curiTemplate != null) {
            return curiTemplate.expandedRelFrom(rel);
        }
        return null;
    }

}
