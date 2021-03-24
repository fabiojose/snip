package io.github.kattlo.snip.templation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.kattlo.util.JSONUtil;

public class ScriptExecutorTest {
    
    @Test
    public void should_execute_command() {

        // setup
        var config = Path.of("./src/test/resources/");
        var basedir = Path.of("./build/tmp/");
        var expected = Path.of(basedir.toString(), "test_script");

        var snipyaml = ConfigurationLoader.load(config);
        var script = JSONUtil.pointer(snipyaml.get()).asObject("#/post/script").get();

        var executor = ScriptExecutor.create(script, basedir);

        // act
        executor.execute();

        // assert
        assertTrue(Files.isDirectory(expected));
        assertTrue(Files.exists(expected));

    }
}
