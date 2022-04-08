package io.github.fabiojose.snip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Test;

import io.github.fabiojose.snip.templation.TemplationFetcher;
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__namespace_=a.namespace",
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-reserver-words",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "my invalid param=with value",
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-does-not-follow",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0-SNAPSHOT",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "My Invalid App Namme",
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
            "--namespace", "my.namespace",
            "--project-version", "$no-a-version",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-version-does-not",
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
            "--namespace", "My Invalid Namespace",
            "--project-version", "1.0.0-SNAPSHOT",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-namespace-does-not-follow",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-0",
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
        var expected = Path.of(TemplationFetcher.SNIP_TMP_DIR, "example");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-0",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-5",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-6",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-7",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-8",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-9",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-10",
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
        var expected = Path.of(directory, "app-name-11/src/to-ignore/__name_.txt");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-11",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-12",
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("version=__version_"));
        }
    }

    @Test
    public void should_not_process_file_content_within_ignored_folder() throws Exception {

        // setup
        var expected = Path.of(directory, "app-name-13/src/to-ignore/__name_.txt");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-13",
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());

        try(var in = new BufferedReader(new FileReader(expected.toFile()))){
            var content = in.lines().collect(Collectors.joining());

            assertTrue(content.contains("param=__version_"));
        }
    }

    @Test
    public void should_process_custom_parameters_in_folder_name() {

        // setup
        var expected = Path.of(directory, "app-name-14/src/app-name-14/MyDomain");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-14",
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(expected.toFile().exists());
    }

    @Test
    public void should_fix_the_parameter_name() {

        // setup
        var expected = Path.of(directory, "app-name-25/src/app-name-25/MyDomain");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "c_domain=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-25",
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
        var expected = Path.of(directory, "app-name-16/src/app-name-16/MyDomainController");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-16",
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
        var expected = Path.of(directory, "app-name-15/src/app-name-15/MyDomain.txt");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-15",
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
        var expected = Path.of(directory, "app-name-17/src/app-name-17/CustomController/CustomDTO.java");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=Custom",
            "-p", "__c_author_=fabiojose",
            "app-name-17",
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
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_custom_=Some Text",
            "-p", "__c_domain_=MyDomain",
            "-p", "__c_author_=fabiojose",
            "app-name-18",
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

    //@Test
    public void should_checkout_remote_template_to_directory() {

        // setup
        var expected = Path.of("/tmp/snip", "fabiojose/dipower-ex/fabiojose-dipower-ex-fd00990");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", "fabiojose/dipower-ex",
            "app-name-20",
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
    public void should_throw_when_custom_placeholder_does_not_follow_the_pattern() {

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=incorrect",
            "-p", "__c_author_=fabiojose",
            "app-name-21",
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(1, exitno);
    }

    @Test
    public void should_be_ok_when_custom_placeholder_does_not_have_pattern() {

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/no-pattern").toURI().toString(),
            "-p", "__c_domain_=domain",
            "-p", "__c_author_=Fábio Jose",
            "app-name-21"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
    }

    @Test
    public void should_throw_when_strict_and_absent_custom_placeholder() {

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "app-name-22"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(1, exitno);
    }

    @Test
    public void should_remove_app_dir_when_exit_non_zero() {

        var expected = Path.of(directory, "app-name-23/");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/example").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "app-name-23"

        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(1, exitno);
        assertFalse(Files.exists(expected));
    }

    @Test
    public void should_be_ok_when_lenient_and_absend_custom_placeholder() {

        var expected = Path.of(directory, "app-name-24/");

        String[] args = {
            "create",
            "-d", directory,
            "--namespace", "my.namespace",
            "--project-version", "1.0.0.Beta",
            "--template", new File(new File(".").getAbsolutePath() + "/src/test/resources/lenient").toURI().toString(),
            "-p", "__c_domain_=MyDomain",
            "app-name-24"
        };

        var command = new CommandLine(entry);

        // act
        var exitno = command.execute(args);

        // assert
        assertEquals(0, exitno);
        assertTrue(Files.exists(expected));
    }
}
