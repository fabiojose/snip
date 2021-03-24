package io.github.kattlo.snip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

public class CreateCommandTest {

    EntryCommand entry = new EntryCommand();

    private String directory = "./build/tmp";

    @Test
    public void should_throw_when_parameters_has_reserved_words() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__s_namespace_=a.namespace"
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);

    }

    @Test
    public void should_throw_when_parameters_does_not_follow_the_pattern() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "my invalid param=with value"
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);

    }

    @Test
    public void should_throw_when_local_template_not_exists() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0",
            "--template", "file:/path/not/exists"
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);
    }

    @Test
    public void should_throw_when_github_template_not_exists() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0",
            "--template", "fabiojose/unknown"
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);
    }

    @Test
    public void should_throw_when_appname_does_not_follow_the_pattern() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "My Invalid App Namme",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0-SNAPSHOT",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);
    }

    @Test
    public void should_throw_when_version_does_not_follow_the_pattern() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "my.namespace",
            "--app-version", "$no-a-version",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);
    }

    @Test
    public void should_throw_when_namespace_does_not_follow_the_pattern() {

        // setup
        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name",
            "--app-namespace", "My Invalid Namespace",
            "--app-version", "1.0.0-SNAPSHOT",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var actual = command.execute(args);

        // assert
        assertEquals(2, actual);
    }

    @Test
    public void should_throw_when_directory_does_not_exists() {

        // setup
        String[] args = {
            "create",
            "-d", "/path/not/exists",
            "-a", "app-name-0",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(2, exitno);
    }

    @Test
    public void should_checkout_local_template_to_directory() {

        // setup
        var expected = Path.of("/tmp/snip", "example");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-0",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        assertFalse(
            FileUtils.listFiles(expected.toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE).isEmpty()
        );

    }

    @Test
    public void should_copy_template_to_app_directory() {

        // setup
        var expected = Path.of(directory, "app-name-5");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-5",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        assertFalse(
            FileUtils.listFiles(expected.toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE).isEmpty()
        );
    }

    @Test
    public void should_delete_git_metadata_dir() {

        // setup
        var expected = Path.of(directory, "app-name-6/.git");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-6",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertFalse(expected.toFile().exists());

    }

    @Test
    public void should_create_folder_hierarchy_when__s_namespace_is_used() {

        // setup
        var expected = Path.of(directory, "app-name-7/src/my/namespace");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-7",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_folder_parameters() {

        // setup
        var expected = Path.of(directory, "app-name-8/src/app-name-8");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-8",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_file_name_parameters() {

        // setup
        var expected = Path.of(directory, "app-name-9/src/my/namespace/app-name-9.java");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-9",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_file_content_parameters() throws IOException {

        // setup
        var expected = Path.of(directory, "app-name-10/src/app-name-10/some-file.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-10",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("version=1.0.0.Beta"));
        }
    }

    @Test
    public void should_not_process_ignored_folder() {

        // setup
        var expected = Path.of(directory, "app-name-11/src/to-ignore/__s_app_.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-11",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_not_process_content_of_ignored_file() throws Exception {

        // setup
        var expected = Path.of(directory, "app-name-12/src/my-file.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-12",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("version=__s_version_"));
        }
    }

    @Test
    public void should_not_process_file_content_within_ignored_folder() throws Exception {

        // setup
        var expected = Path.of(directory, "app-name-13/src/to-ignore/__s_app_.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-13",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("param=__s_version"));
        }
    }

    @Test
    public void should_process_custom_parameters_in_folder_name() {

        // setup
        var expected = Path.of(directory, "app-name-14/src/app-name-14/my-domain");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-14",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=my-domain"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_custom_parameters_in_folder_name_whith_more_text() {

        // setup
        var expected = Path.of(directory, "app-name-16/src/app-name-16/domainController");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-16",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=domain"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_custom_parameters_in_file_name() {

        // setup
        var expected = Path.of(directory, "app-name-15/src/app-name-15/my-custom.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-15",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=my-custom"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_custom_parameters_in_file_name_with_more_text() {

        // setup
        var expected = Path.of(directory, "app-name-17/src/app-name-17/customController/customDTO.java");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-17",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=custom"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_process_custom_parameters_in_file_content() throws Exception {

        // setup
        var expected = Path.of(directory, "app-name-18/src/app-name-18/my/namespace/another-file.txt");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-18",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_custom_=Some Text"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("custom=Some Text"));
        }
    }

    @Test
    public void should_checkout_remote_template_to_directory() {

        // setup
        var expected = Path.of("/tmp/snip", "fabiojose/dipower-ex/fabiojose-dipower-ex-fd00990");

        String[] args = {
            "create",
            "-d", directory,
            "-a", "app-name-20",
            "--app-namespace", "my.namespace",
            "--app-version", "1.0.0.Beta",
            "--template", "fabiojose/dipower-ex",
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        assertFalse(
            FileUtils.listFiles(expected.toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE).isEmpty()
        );
    }

    @Test
    public void should_throw_when_custom_placeholder_does_not_follow_the_rule() {

    }

    @Test
    public void should_throw_when_strict_and_absent_custom_placeholder() {

    }

    @Test
    public void should_throw_when_builtin_placeholder_does_not_follow_the_rule() {

    }

    @Test
    public void should_be_ok_when_lenient_and_absend_custom_placeholder() {

    }

    @Test
    public void should_process_many_placeholder_occurrences_in_file_content() {

    }
}
