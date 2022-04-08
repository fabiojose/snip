package io.github.fabiojose.snip.context;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author fabiojose
 */
@EqualsAndHashCode(of = "placeholder")
@ToString(of = {"label", "pattern"})
public class Rule {

    @Getter
    private String placeholder;
    private Pattern pattern;

    @Getter
    private String label;

    public Rule(String placeholder, String pattern, String label){
        this.placeholder = Objects.requireNonNull(placeholder);

        this.pattern = Optional.ofNullable(pattern)
            .map(Pattern::compile)
            .orElseGet(() -> Pattern.compile(".*"));

        this.label = Optional.ofNullable(label)
            .orElseGet(() -> placeholder);
    }

    public void validate(String value) {

        if(!pattern.matcher(Objects.requireNonNull(value)).matches()){
            throw new IllegalPlaceholderValueException(value);
        }

    }

}
