package io.github.kattlo.snip.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class IncludeTest {

    @Test
    public void should_load_directories_to_ignore() {

        // setup
        var ignore = Path.of("./src/test/resources/.snipignore");

        // act
        var actual = Include.create(ignore);

        // assert
        assertEquals(3 + Include.ALWAYS_IGNORE_FOLDER.size(),
            actual.getFolders().size());
    }

    @Test
    public void should_load_wildcards_to_ignore() {

        // setup
        var ignore = Path.of("./src/test/resources/.snipignore");

        // act
        var actual = Include.create(ignore);

        // assert
        assertEquals(3, actual.getWildcards().size());

    }

    @Test
    public void should_load_files_to_ignore() {

        // setup
        var ignore = Path.of("./src/test/resources/.snipignore");

        // act
        var actual = Include.create(ignore);

        // assert
        assertEquals(3 + Include.ALWAYS_IGNORE_FILE.size(),
            actual.getFiles().size());

    }

    @Test
    public void should_ignore_folder() {

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.folder(Path.of("./src/test/resources/example/build/tmp"));

        // assert
        assertFalse(actual);
    }

    @Test
    public void should_not_ignore_folder_without_ignore() {

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.folder(Path.of("./src/test/resources/example/not-ignored-folder"));

        // assert
        assertTrue(actual);
    }

    @Test
    public void should_ignore_wildcard() {

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.wildcard(Path.of("./src/test/resources/example/src/mi-file.png"));

        // assert
        assertFalse(actual);
    }

    @Test
    public void should_not_ignore_resource_without_ignore() {

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.wildcard(Path.of("./src/test/resources/example/src/Some.java"));

        // assert
        assertTrue(actual);
    }

    @Test
    public void should_ignore_file(){

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.file(Path.of("./src/test/resources/example/src/my-file.txt"));

        // assert
        assertFalse(actual);
    }

    @Test
    public void should_not_ignore_file_without_ignore() {

        // setup
        var file = Path.of("./src/test/resources/.snipignore");
        var include = Include.create(file);

        // act
        var actual = include.file(Path.of("./src/test/resources/example/build.txt"));

        // assert
        assertTrue(actual);
    }
}
