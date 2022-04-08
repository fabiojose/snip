package io.github.fabiojose.snip.context;

import java.nio.file.Path;
import java.util.Objects;

import lombok.Getter;

/**
 * @author fabiojose
 */
@Getter
public class Context {

    public static final String NAMESPACE_PARAM = "__namespace_";
    public static final String NAME_PARAM = "__name_";
    public static final String VERSION_PARAM = "__version_";

    public static final String SNIP_IGNORE =  ".snipignore";

    private Include include;
    private Placeholders placeholders;
    private Path template;
    private Path target;

    private Context() {}

    public static Context create(Placeholders placeholders, Path template, Path target) {

        var result = new Context();
        result.include = Include.create(Path.of(template.toString(), SNIP_IGNORE));

        result.placeholders = Objects.requireNonNull(placeholders);
        result.template = Objects.requireNonNull(template);
        result.target = Objects.requireNonNull(target);

        return result;
    }
}
