package io.github.fabiojose.snip.model;

import io.github.fabiojose.snip.context.Placeholders;
import java.util.Objects;

public class ProjectName {

    private String name;

    /**
     * @throws IllegalArgumentException When name does not follow the pattern {@link Placeholders#PLACEHOLDER_VALUE_PATTERN}
     */
    public ProjectName(String name) {
        if (
            !Placeholders.PLACEHOLDER_VALUE_PATTERN
                .matcher(Objects.requireNonNull(name))
                .matches()
        ) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
