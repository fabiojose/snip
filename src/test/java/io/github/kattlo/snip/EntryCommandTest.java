package io.github.kattlo.snip;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

public class EntryCommandTest {

    EntryCommand entry = new EntryCommand();

    String actualHome;

    @BeforeEach
    public void beforeEach() {

        actualHome = System.getProperty("user.home");
        System.setProperty("user.home", System.getProperty("java.io.tmpdir"));

    }

    @AfterEach
    public void afterEach() {

        System.setProperty("user.home", actualHome);

    }

    @Test
    public void should_throw_when_can_not_create_the_home_dir() {

        // setup
        String [] args = {
            "create",
            "--app-name=app",
            "--app-namespace=namespace",
            "--template=template"
        };

        var command = new CommandLine(entry);
        System.setProperty("user.home", "/path/not/exists");

        // act
        var exitno = command.execute(args);

        // assert
        assertNotEquals(0, exitno);

    }
   
    @Test
    public void should_create_home_first_run() {

        // setup
        String [] args = {
            "create",
            "--app-name=app",
            "--app-namespace=namespace",
            "--template=template"
        };

        var command = new CommandLine(entry);

        // act
        command.execute(args);

        // assert
        var actual = Path.of(System.getProperty("user.home"), BaseCommand.HOME_DIR);
        assertTrue(actual.toFile().exists());
    }

    @Test
    public void should_create_config_first_run() {

        // setup
        String [] args = {
            "create",
            "--app-name=app",
            "--app-namespace=namespace",
            "--template=template"
        };

        var command = new CommandLine(entry);

        // act
        command.execute(args);

        // assert
        var home = Path.of(System.getProperty("user.home"), BaseCommand.HOME_DIR);
        var actual = new File(home.toFile(), BaseCommand.CONFIG_FILE_NAME);

        assertTrue(actual.exists());
    }

    @Test
    public void should_load_the_config_from_home() {

    }
}
