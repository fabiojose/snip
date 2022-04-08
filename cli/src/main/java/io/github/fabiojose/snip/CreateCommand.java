package io.github.fabiojose.snip;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import io.github.fabiojose.snip.context.Context;
import io.github.fabiojose.snip.context.IllegalPlaceholderException;
import io.github.fabiojose.snip.context.Placeholders;
import io.github.fabiojose.snip.context.ReservedPlaceholderException;
import io.github.fabiojose.snip.processor.Processor;
import io.github.fabiojose.snip.templation.ConfigurationLoader;
import io.github.fabiojose.snip.templation.ScriptExecutor;
import io.github.fabiojose.snip.templation.TemplationFetcher;
import io.github.fabiojose.snip.templation.TemplationNotFoundException;
import io.github.fabiojose.snip.util.JSONUtil;
import io.github.fabiojose.snip.util.Reporter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "create",
    aliases = {
        "c",
        "scaffold"
    },
    description = "To create project based on templation",
    mixinStandardHelpOptions = true
)
@Slf4j
public class CreateCommand implements Runnable {

    @Spec
    CommandSpec spec;

    private Reporter reporter = Reporter.create();
    private Path directory;
    private String name;
    private TemplationFetcher fetcher;
    private final Placeholders.PlaceholdersBuilder placeholders =
        Placeholders.builder();

    private void validate(String value, String argName) {

        if(!Placeholders.OPTION_PATTERN.matcher(value).matches()){
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Invalid value of " + argName);
        }

    }

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Directory to hold the new project (default to current)",
        descriptionKey = "/path/to/",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        defaultValue = ".",
        required = true
    )
    public void setDirectory(Path directory) {
        if(directory.toFile().exists()){
            this.directory = directory;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Directory does not exists " + directory);
        }
    }

    @Parameters(
        arity = "1",
        description = "The value of __name_ placeholder",
        descriptionKey = "project-name",
        paramLabel = "project-name"
    )
    public void setName(String name) {
        validate(name, "name");
        this.name = name;

        placeholders.name(name);
    }

    @Option(
        names = {
            "--project-version"
        },
        description = "The value of __version_ placeholder",
        descriptionKey = "project version",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        defaultValue = "1.0.0",
        required = true
    )
    public void setVersion(String version){
        validate(version, "--project-version");
        placeholders.version(version);
    }

    @Option(
        names = {
            "-n",
            "--namespace"
        },
        description = "The value of __namespace_ placeholder",
        descriptionKey = "namespace",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        defaultValue = "com.example",
        required = true
    )
    public void setNamespace(String namespace){
        validate(namespace, "--namespace");
        placeholders.namespace(namespace);
    }

    @Option(
        names = {
            "-t",
            "--templation",
            "--template"
        },
        description = {
            "The templation to create the app",
            "Should be in the local file system: file:/path/to/",
            "Or remote within Github: user/repo"
        },
        descriptionKey = "templation",
        required = true
    )
    public void setTemplation(URI templation) {

        try {
            this.fetcher = TemplationFetcher.create(templation);
        }catch(IOException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    "Unable to reach the templation location", e);
        }catch(TemplationNotFoundException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    "Templation not found", e);
        }

    }

    @Option(
        names = {
            "-p"
        },
        description = "Custom placeholders used by a specific templation",
        paramLabel = "placeholder=my-value"
    )
    public void setParameters(String[] parameters) {
        placeholders.parameters(Arrays.asList(parameters));
    }

    @Override
    public void run() {

        boolean succcess = false;
        var appdir = Path.of(directory.toString(), name);
        try{
            // checkout to /tmp/snip/
            var template = fetcher.fetch();

            FileUtils.copyDirectory(template.toFile(), appdir.toFile());
            log.debug("template copied to app directory at {}", appdir);

            // load .snip.yml, if any
            var config = ConfigurationLoader.load(appdir);

            // custom placeholder rules, if any
            config.
                flatMap(c -> JSONUtil.pointer(c).asObject("#/placeholders"))
                .ifPresent(placeholders::rules);

            // remove .git folder (if exists)
            var gitdir = Path.of(appdir.toString(), ".git");
            FileUtils.deleteDirectory(gitdir.toFile());

            var context = Context.create(placeholders.build(), template, appdir);

            // process folders parameters
            Processor.forDirectories().process(context);;

            // process file name parameters
            Processor.forFiles().process(context);

            // process file content parameters
            Processor.forContent().process(context);

            // run post script, if any
            config
                .flatMap(c -> JSONUtil.pointer(c).asObject("#/post/script"))
                .ifPresent(script ->
                    ScriptExecutor.create(script, appdir).execute());

            reporter.success("New app created at: " + appdir);
            succcess = true;

        }catch(IOException | URISyntaxException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }catch(ReservedPlaceholderException | IllegalPlaceholderException e){
            throw new CommandLine.ParameterException(spec.commandLine(),
                e.getMessage(), e);
        }finally{
            // delete appdir when there no success
            if(!succcess){
                try{
                    FileUtils.forceDelete(appdir.toFile());
                }catch(IOException e) {
                    System.err.println("Can not delete " + appdir);
                    e.printStackTrace();
                }
            }
        }

    }
}