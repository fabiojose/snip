package io.github.kattlo.snip.context;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fabiojose
 */
@Getter
@Setter
public class Context {

    static final String SNIP_IGNORE =  ".snipignore";
    
    private Include include;
    private Map<String, String> placeholders;
    private Path template;
    private Path target;

    private Context() {}

    public static Context create(Map<String, String> placeholders, Path template, Path target) {

        var result = new Context();
        result.include = Include.create(Path.of(template.toString(), SNIP_IGNORE));

        result.placeholders = Collections.unmodifiableMap(placeholders);
        result.template = template;
        result.target = target;

        return result;
    }
}
