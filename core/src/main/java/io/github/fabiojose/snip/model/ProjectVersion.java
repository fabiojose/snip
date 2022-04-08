package io.github.fabiojose.snip.model;

import java.util.Objects;

import io.github.fabiojose.snip.context.Placeholders;

public class ProjectVersion {

    private String version;

    /**
     * @throws IllegalArgumentException When version does not follow the pattern {@link Placeholders#PLACEHOLDER_VALUE_PATTERN}
     */
    public ProjectVersion(String version) {
        if (
            !Placeholders.PLACEHOLDER_VALUE_PATTERN
                .matcher(Objects.requireNonNull(version))
                .matches()
        ) {
            throw new IllegalArgumentException("Invalid version: " + version);
        }
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }
}
