package io.github.kattlo.snip.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.kattlo.util.JSONUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Getter
@Slf4j
public class Placeholders {

    public static final String PLACEHOLDER_PATTERN_STRING =
        "__[0-9a-zA-Z]+_[0-9a-zA-Z]+_";

    public static final Pattern PLACEHOLDER_PATTERN =
        Pattern.compile("(" + PLACEHOLDER_PATTERN_STRING + ")");

    public static final Pattern OPTION_PATTERN =
        Pattern.compile("^[\\w\\.\\-]+$");

    public static final Pattern PARAM_PATTERN =
        Pattern.compile("^" + PLACEHOLDER_PATTERN_STRING + "=.+$");

    private static final List<String> RESERVED_WORDS = List.of(
        Context.APP_PARAM,
        Context.VERSION_PARAM,
        Context.NAMESPACE_PARAM
    );

    private static final String EQUALS = "=";

    private String appname;
    private String version;
    private String namespace;

    @Getter(AccessLevel.MODULE)
    private List<String> parameters;

    @Getter(AccessLevel.MODULE)
    private Map<String, String> placeholders;

    // TODO validate custom placeholders patterns
    @Getter(AccessLevel.MODULE)
    private Map<String, String> customPlaceholders;

    @Getter(AccessLevel.MODULE)
    private JSONObject rules;

    @Getter(AccessLevel.MODULE)
    private Map<String, String> customPlaceholdersRules;

    @Builder(toBuilder = true)
    private static Placeholders create(String appname, String version, String namespace,
        List<String> parameters, JSONObject rules){

        var result = new Placeholders();

        result.placeholders = new HashMap<>();
        result.placeholders.put(Context.APP_PARAM, appname);
        result.placeholders.put(Context.VERSION_PARAM, version);
        result.placeholders.put(Context.NAMESPACE_PARAM, namespace);

        var invalid = Optional.ofNullable(parameters)
            .orElseGet(() -> List.of())
            .stream()
            .filter(p -> !PARAM_PATTERN.matcher(p).matches())
            .collect(Collectors.toList());

        if(!invalid.isEmpty()){
            throw new IllegalPlaceholderException(invalid.toString());
        }

        var reserved = Optional.ofNullable(parameters)
            .orElseGet(() -> List.of())
            .stream()
            .map(p -> p.split(EQUALS))
            .map(p -> p[0])
            .filter(RESERVED_WORDS::contains)
            .collect(Collectors.toList());

        if(!reserved.isEmpty()){
            throw new ReservedPlaceholderException(reserved.toString());
        }

        result.customPlaceholders = Optional.ofNullable(parameters)
            .orElseGet(() -> List.of())
            .stream()
            .map(p -> p.split(EQUALS))
            .collect(Collectors.toMap((p) -> p[0], (p) -> p[1]));
        log.info("custom placeholders {}", result.customPlaceholders);

        result.placeholders.putAll(result.customPlaceholders);
        result.placeholders = Collections.unmodifiableMap(result.placeholders);

        result.appname = Objects.requireNonNull(appname);
        result.version = Objects.requireNonNull(version);
        result.namespace = Objects.requireNonNull(namespace);

        var customRules = Optional.ofNullable(rules)
            .flatMap(r -> JSONUtil.pointer(r).asArray("#/spec"))
            .map(JSONArray::toList)
            .orElseGet(() -> List.of())
            .stream()
            .map(o -> (Map<String, String>)o)
            .map(o -> new Rule(o.get("name"), o.get("pattern"), o.get("label")))
            .collect(Collectors.toMap(Rule::getPlaceholder, r -> r));

        log.debug("custom placeholder rules {}", customRules);

        var customStrict = Optional.ofNullable(rules)
            .flatMap(r -> JSONUtil.pointer(r).asBoolean("#/strict"))
            .orElseGet(() -> Boolean.TRUE);
        log.info("custom placeholder strict: {}", customStrict);

        if(customStrict) {

            var missing = customRules.entrySet()
                .stream()
                .filter(kv -> result.customPlaceholders.containsKey(kv.getKey()))
                .map(Entry::getKey)
                .collect(Collectors.toList());

            // validate if any of the is absend
            if(!missing.isEmpty()){
                log.debug("missing custom placeholders {}", missing);
                throw new MissingPlaceholderException(missing.toString());
            }
        }

        // validate just the present placeholders
        result.placeholders.entrySet()
            .stream()
            .filter(kv -> customRules.containsKey(kv.getKey()))
            .forEach(kv -> {
                log.debug("validating placeholder {}", kv);

                var rule = customRules.get(kv.getKey());
                rule.validate(kv.getValue());

            });

        return result;
    }

    public Map<String, String> entries() {
        return placeholders;
    }

}
