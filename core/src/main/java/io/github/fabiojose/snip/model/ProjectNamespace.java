package io.github.fabiojose.snip.model;

import java.util.Objects;

import io.github.fabiojose.snip.context.Placeholders;

public class ProjectNamespace {

    private String namespace;

    /**
     * @throws IllegalArgumentException When namespace does not follow the pattern {@link Placeholders#PLACEHOLDER_VALUE_PATTERN}
     */
    public ProjectNamespace(String namespace) {
        if (
            !Placeholders.PLACEHOLDER_VALUE_PATTERN
                .matcher(Objects.requireNonNull(namespace))
                .matches()
        ) {
            throw new IllegalArgumentException("Invalid namespace: " + namespace);
        }
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }
}
