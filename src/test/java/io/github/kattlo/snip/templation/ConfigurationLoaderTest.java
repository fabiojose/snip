package io.github.kattlo.snip.templation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

public class ConfigurationLoaderTest {

    @Test
    public void should_throw_when_not_found_in_load_as_map() {

        // setup
        var config = Path.of("./src/test/resources/.snip-not-found.yml");

        // act
        assertThrows(UncheckedIOException.class, 
            () -> ConfigurationLoader.loadAsMap(config));

    }

    @Test
    public void should_throw_yaml_not_follow_the_schema() {

        // setup
        var config = Path.of("./src/test/resources/.snip-invalid-placeholder.yml");

        // act
        var actual = assertThrows(ValidationException.class, 
            () -> ConfigurationLoader.loadFile(config));

        var errors = actual.getAllMessages().stream()
            .filter(m -> m.contains("_invalid_"))
            .collect(Collectors.toList());

        assertFalse(errors.isEmpty());
    }

    @Test
    public void should_result_empty_when_no_snip_yml() {

        // setup
        var config = Path.of("./src/test/resources/.snip-not-file.yml");

        // act
        var actual = ConfigurationLoader.loadFile(config);

        assertTrue(actual.isEmpty());

    }

    @Test
    public void should_result_json_object_when_yaml_follow_the_schema() {

        // setup
        var config = Path.of("./src/test/resources/");

        // act
        var actual = ConfigurationLoader.load(config);

        assertTrue(actual.isPresent());

    }
}
