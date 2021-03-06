package de.otto.edison.hal.odyssey.model;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;
import de.otto.edison.hal.Link;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class LinkModel {

    private static final EnumSet<Operator> optionalParamOperators = EnumSet.of(
            Operator.QUERY,
            Operator.CONTINUATION);

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

    public LinkModel(final Link halLink) {
        this.id = UUID.randomUUID().toString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkModel linkModel = (LinkModel) o;
        return Objects.equals(href, linkModel.href) &&
                Objects.equals(hrefParams, linkModel.hrefParams) &&
                Objects.equals(templated, linkModel.templated) &&
                Objects.equals(type, linkModel.type) &&
                Objects.equals(hreflang, linkModel.hreflang) &&
                Objects.equals(title, linkModel.title) &&
                Objects.equals(name, linkModel.name) &&
                Objects.equals(deprecation, linkModel.deprecation) &&
                Objects.equals(profile, linkModel.profile) &&
                Objects.equals(id, linkModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, hrefParams, templated, type, hreflang, title, name, deprecation, profile, id);
    }
}
