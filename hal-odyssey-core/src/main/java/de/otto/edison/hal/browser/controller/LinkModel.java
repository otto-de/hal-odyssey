package de.otto.edison.hal.browser.controller;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;
import de.otto.edison.hal.CuriTemplate;
import de.otto.edison.hal.Link;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * A link to a REST resource.
 *
 * @see <a href="https://tools.ietf.org/html/draft-kelly-json-hal-08#section-5">draft-kelly-json-hal-08#section-5</a>
 * @since 0.1.0
 */
public class LinkModel {

    private static final EnumSet<Operator> optionalParamOperators = EnumSet.of(
            Operator.QUERY,
            Operator.CONTINUATION);
    private static final Map<String,String> halRels = Collections.singletonMap(
            "curies", "Reserved link relation which can be used to hint at the location of resource documentation.");
    private static final Map<String,String> ianaRels = new HashMap<String,String>() {{
        put("about",               "Refers to a resource that is the subject of the link's context.");
        put("alternate",           "Refers to a substitute for this context");
        put("appendix",            "Refers to an appendix.");
        put("archives",            "Refers to a collection of records, documents, or other materials of historical interest.");
        put("author",              "Refers to the context's author.");
        put("blocked-by",          "Identifies the entity that blocks access to a resource following receipt of a legal demand.");
        put("bookmark",            "Gives a permanent link to use for bookmarking purposes.");
        put("canonical",           "Designates the preferred version of a resource (the IRI and its contents).");
        put("chapter",             "Refers to a chapter in a collection of resources.");
        put("cite-as",             "Indicates that the link target is preferred over the link context for the purpose of referencing.");
        put("collection",          "The target IRI points to a resource which represents the collection resource for the context IRI.");
        put("contents",            "Refers to a table of contents.");
        put("convertedFrom",       "The document linked to was later converted to the document that contains this link relation. For example, an RFC can have a link to the Internet-Draft that became the RFC; in that case, the link relation would be \"convertedFrom\".");
        put("copyright",           "Refers to a copyright statement that applies to the link's context.");
        put("create-form",         "The target IRI points to a resource where a submission form can be obtained.");
        put("current",             "Refers to a resource containing the most recent item(s) in a collection of resources.");
        put("desribedby",          "Refers to a resource providing information about the link's context.");
        put("describes",           "The relationship A 'describes' B asserts that resource A provides a description of resource B. There are no constraints on the format or representation of either A or B, neither are there any further constraints on either resource.");
        put("disclosure",          "Refers to a list of patent disclosures made with respect to material for which 'disclosure' relation is specified.");
        put("dns-prefetch",        "Used to indicate an origin that will be used to fetch required resources for the link context, and that the user agent ought to resolve as early as possible.");
        put("duplicate",           "Refers to a resource whose available representations are byte-for-byte identical with the corresponding representations of the context IRI.");
        put("edit",                "Refers to a resource that can be used to edit the link's context.");
        put("edit-form",           "The target IRI points to a resource where a submission form for editing associated resource can be obtained.");
        put("edit-media",          "Refers to a resource that can be used to edit media associated with the link's context.");
        put("enclosure",           "Identifies a related resource that is potentially large and might require special handling.");
        put("first",               "An IRI that refers to the furthest preceding resource in a series of resources.");
        put("glossary",            "Refers to a glossary of terms.");
        put("help",                "Refers to context-sensitive help.");
        put("hosts",               "Refers to a resource hosted by the server indicated by the link context.");
        put("hub",                 "Refers to a hub that enables registration for notification of updates to the context.");
        put("icon",                "Refers to an icon representing the link's context.");
        put("index",               "Refers to an index.");
        put("item",                "The target IRI points to a resource that is a member of the collection represented by the context IRI.");
        put("last",                "An IRI that refers to the furthest following resource in a series of resources.");
        put("lastest-version",     "Points to a resource containing the latest (e.g., current) version of the context.");
        put("license",             "Refers to a license associated with this context.");
        put("lrdd",                "Refers to further information about the link's context, expressed as a LRDD (\"Link-based Resource Descriptor Document\") resource. See [RFC6415] for information about processing this relation type in host-meta documents. When used elsewhere, it refers to additional links and other metadata. Multiple instances indicate additional LRDD resources. LRDD resources MUST have an \"application/xrd+xml\" representation, and MAY have others.");
        put("memento",             "The Target IRI points to a Memento, a fixed resource that will not change state anymore.");
        put("monitor",             "Refers to a resource that can be used to monitor changes in an HTTP resource.");
        put("monitor-group",       "Refers to a resource that can be used to monitor changes in a specified group of HTTP resources.");
        put("next",                "Indicates that the link's context is a part of a series, and that the next in the series is the link target.");
        put("next-archive",        "Refers to the immediately following archive resource.");
        put("nofollow",            "Indicates that the context’s original author or publisher does not endorse the link target.");
        put("noreferrer",          "Indicates that no referrer information is to be leaked when following the link.");
        put("original",            "The Target IRI points to an Original Resource.");
        put("payment",             "Indicates a resource where payment is accepted.");
        put("pingback",            "Gives the address of the pingback resource for the link context.");
        put("preconnect",          "Used to indicate an origin that will be used to fetch required resources for the link context. Initiating an early connection, which includes the DNS lookup, TCP handshake, and optional TLS negotiation, allows the user agent to mask the high latency costs of establishing a connection.");
        put("predecessor-version", "Points to a resource containing the predecessor version in the version history.");
        put("prefetch",            "The prefetch link relation type is used to identify a resource that might be required by the next navigation from the link context, and that the user agent ought to fetch, such that the user agent can deliver a faster response once the resource is requested in the future.");
        put("preload",             "Refers to a resource that should be loaded early in the processing of the link's context, without blocking rendering.");
        put("prerender",           "Used to identify a resource that might be required by the next navigation from the link context, and that the user agent ought to fetch and execute, such that the user agent can deliver a faster response once the resource is requested in the future.");
        put("prev",                "Indicates that the link's context is a part of a series, and that the previous in the series is the link target.");
        put("preview",             "Refers to a resource that provides a preview of the link's context.");
        put("previous",            "Refers to the previous resource in an ordered series of resources. Synonym for \"prev\".");
        put("prev-archive",        "Refers to the immediately preceding archive resource.");
        put("privace-policy",      "Refers to a privacy policy associated with the link's context.");
        put("profile",             "Identifying that a resource representation conforms to a certain profile, without affecting the non-profile semantics of the resource representation.");
        put("related",             "Identifies a related resource.");
        put("restconf",            "Identifies the root of RESTCONF API as configured on this HTTP server. The \"restconf\" relation defines the root of the API defined in RFC8040. Subsequent revisions of RESTCONF will use alternate relation values to support protocol versioning.");
        put("replies",             "Identifies a resource that is a reply to the context of the link.");
        put("search",              "Refers to a resource that can be used to search through the link's context and related resources.");
        put("section",             "Refers to a section in a collection of resources.");
        put("self",                "Conveys an identifier for the link's context.");
        put("service",             "Indicates a URI that can be used to retrieve a service document.");
        put("start",               "Refers to the first resource in a collection of resources.");
        put("stylesheet",          "Refers to a stylesheet.");
        put("subsection",          "Refers to a resource serving as a subsection in a collection of resources.");
        put("successor-version",   "Points to a resource containing the successor version in the version history.");
        put("tag",                 "Gives a tag (identified by the given address) that applies to the current document.");
        put("terms-of-service",    "Refers to the terms of service associated with the link's context.");
        put("timegate",            "The Target IRI points to a TimeGate for an Original Resource.");
        put("timemap",             "The Target IRI points to a TimeMap for an Original Resource.");
        put("type",                "Refers to a resource identifying the abstract semantic type of which the link's context is considered to be an instance.");
        put("up",                  "Refers to a parent document in a hierarchy of documents.");
        put("version-history",     "Points to a resource containing the version history for the context.");
        put("via",                 "Identifies a resource that is the source of the information in the link's context.");
        put("webmention",          "Identifies a target URI that supports the Webmention protcol. This allows clients that mention a resource in some form of publishing process to contact that endpoint and inform it that this resource has been mentioned.");
        put("working-copy",        "Points to a working copy for this resource.");
        put("working-copy-of",     "Points to the versioned resource from which this working copy was obtained.");
    }};

    public static final String IANA_RELS_HREF = "https://www.iana.org/assignments/link-relations/link-relations.xhtml";
    public static final String HAL_JSON_HREF = "https://tools.ietf.org/html/draft-kelly-json-hal-08";

    public final String rel;
    public final String relHref;
    public final String relDesc;
    public final String href;
    public final List<Map<String,Object>> hrefParams;
    public final Boolean templated;
    public final String type;
    public final String hreflang;
    public final String title;
    public final String name;
    public final String deprecation;
    public final String profile;
    public final String id;

    public LinkModel(final Link halLink, final Optional<CuriTemplate> curiTemplate) {
        this.id = UUID.randomUUID().toString();
        this.rel = curiTemplate.isPresent()
                ? curiTemplate.get().curiedRelFrom(halLink.getRel())
                : halLink.getRel();
        this.relHref = getRelHref(halLink, curiTemplate);
        this.relDesc = getRelDesc(halLink, curiTemplate);
        this.href = halLink.getHref();
        this.hrefParams = halLink.isTemplated()
                ? paramMap(halLink.getHrefAsTemplate())
                : Collections.emptyList();
        this.templated = halLink.isTemplated();
        this.type = halLink.getType();
        this.hreflang = halLink.getHreflang();
        this.title = halLink.getTitle();
        this.name = halLink.getName();
        this.deprecation = halLink.getDeprecation();
        this.profile = halLink.getProfile();
    }

    private List<Map<String, Object>> paramMap(final UriTemplate uriTemplate) {
        final List<String> params = asList(uriTemplate.getVariables());
        final Set<String> requiredParams = new HashSet<>();
        stream(uriTemplate.getExpressions()).forEach(expression -> {
            if (!optionalParamOperators.contains(expression.getOperator())) {
                requiredParams.addAll(expression
                        .getVarSpecs()
                        .stream()
                        .map(VarSpec::getVariableName)
                        .collect(toList())
                );
            }
        });
        return params
                .stream()
                .map(param -> new HashMap<String,Object>() {{
                    put("key", param);
                    put("required", requiredParams.contains(param));
                }})
                .collect(toList());
    }

    private String getRelHref(final Link halLink,
                              final Optional<CuriTemplate> curiTemplate) {
        if (halLink.getRel().startsWith("http://") || halLink.getRel().startsWith("https://")) {
            return halLink.getRel();
        }
        if (ianaRels.containsKey(halLink.getRel())) {
            return IANA_RELS_HREF;
        }
        if (halRels.containsKey(halLink.getRel())) {
            return HAL_JSON_HREF;
        }
        if (curiTemplate.isPresent()) {
            return curiTemplate.get().expandedRelFrom(this.rel);
        }
        return null;
    }

    private String getRelDesc(final Link halLink,
                              final Optional<CuriTemplate> curiTemplate) {
        if (ianaRels.containsKey(halLink.getRel())) {
            return ianaRels.get(halLink.getRel());
        }
        if (halRels.containsKey(halLink.getRel())) {
            return halRels.get(halLink.getRel());
        }
        if (curiTemplate.isPresent()) {
            return curiTemplate.get().expandedRelFrom(this.rel);
        }
        return null;
    }
}
