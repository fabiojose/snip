package io.github.fabiojose.snip;

import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import io.github.fabiojose.snip.context.IllegalPlaceholderException;
import io.github.fabiojose.snip.context.Placeholders;
import io.github.fabiojose.snip.context.ReservedPlaceholderException;
import io.github.fabiojose.snip.templation.TemplationNotFoundException;
import io.github.fabiojose.snip.util.Reporter;
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
public class CreateCommand implements Runnable {

    @Spec
    CommandSpec spec;

    private Reporter reporter = Reporter.create();
    private Path directory;
    private String name;
    private URI templateLocation;
    private String projectVersion;
    private String projectNamespace;

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
        this.projectVersion = version;
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
        this.projectNamespace = namespace;
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
        this.templateLocation = templation;
    }

    @Option(
        names = { "-p" },
        paramLabel = "NAME=VALUE",
        description = "Custom placeholders used by a specific templation",
        required = false
    )
    Map<String, String> customPlaceholders;

    @Override
    public void run() {

        var templation = Templation.newBuilder()
            .withLocation(this.templateLocation)
            .withProjectLocation(this.directory)
            .withProjectName(this.name)
            .withProjectVersion(this.projectVersion)
            .withProjectNamespace(this.projectNamespace)
            .withPlaceholders(Optional.ofNullable(this.customPlaceholders).orElseGet(() -> Map.of()))
            .build();

        try {

            var newProjectLocation = templation.newProject();
            reporter.success("New app created at: " + newProjectLocation);

        }catch(UncheckedIOException | TemplationNotFoundException e) {
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }catch(ReservedPlaceholderException | IllegalPlaceholderException e){
            throw new CommandLine.ParameterException(spec.commandLine(),
                e.getMessage(), e);
        }
    }
}
