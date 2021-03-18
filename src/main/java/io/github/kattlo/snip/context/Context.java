package io.github.kattlo.snip.context;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fabiojose
 */
@Getter
@Setter
public class Context {

    public static final String NAMESPACE_PARAM = "__s_namespace_";
    public static final String APP_PARAM = "__s_app_";
    public static final String VERSION_PARAM = "__s_version_";

    public static final String PLACEHOLDER_PATTERN_STRING = 
        "__[0-9a-zA-Z]+_[0-9a-zA-Z]+_";

    public static final Pattern PLACEHOLDER_PATTERN = 
        Pattern.compile("(" + PLACEHOLDER_PATTERN_STRING + ")");

    public static final Pattern OPTION_PATTERN = 
        Pattern.compile("^[\\w\\.\\-]+$");

    public static final Pattern PARAM_PATTERN = 
        Pattern.compile("^" + PLACEHOLDER_PATTERN_STRING + "=.+$");

    public static final String SNIP_IGNORE =  ".snipignore";
    
    private Include include;
    private Map<String, String> placeholders;
    private Path template;
    private Path target;

    private String namespace;
    private String appname;
    private String version;

    private Context() {}

    public static Context create(Map<String, String> placeholders, Path template, Path target) {

        var result = new Context();
        result.include = Include.create(Path.of(template.toString(), SNIP_IGNORE));

        result.placeholders = Collections.unmodifiableMap(placeholders);
        result.template = template;
        result.target = target;

        result.namespace = placeholders.get(Context.NAMESPACE_PARAM);
        result.appname = placeholders.get(Context.APP_PARAM);
        result.version = placeholders.get(Context.VERSION_PARAM);

        return result;
    }
}
