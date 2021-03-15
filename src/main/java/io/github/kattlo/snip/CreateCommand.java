package io.github.kattlo.snip;

import java.net.URI;
import java.nio.file.Path;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@Command(
    name = "create",
    description = "To create app based on template",
    mixinStandardHelpOptions = true
)
public class CreateCommand extends BaseCommand {
    
    @Spec
    CommandSpec spec;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Directory to hold the new app (default to current)'",
        descriptionKey = "/path/to/",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        defaultValue = ".",
        required = true
    )
    Path directory;

    @Option(
        names = {
            "-a",
            "--app",
            "--app-name"
        },
        description = "The name of app to create",
        descriptionKey = "app name",
        required = true
    )
    String app;

    @Option(
        names = {
            "--app-version"
        },
        description = "The version of app to create",
        descriptionKey = "app version",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        defaultValue = "1.0.0",
        required = true
    )
    String version;

    @Option(
        names = {
            "-n",
            "--namespace",
            "--app-namespace"
        },
        description = "The namespace or package of the app to create",
        descriptionKey = "namespace",
        required = true
    )
    String namespace;

    @Option(
        names = {
            "-p"
        },
        description = "Custom parameters used by the template",
        paramLabel = "param=value",
        descriptionKey = "placeholder"
    )
    private String[] placeholders;

    @Option(
        names = {
            "-t",
            "--template"
        },
        description = "The template to create the app",
        descriptionKey = "template",
        required = true
    )
    URI template;

    @Override
    public void run() {
        super.run();
       
        // TODO
    }

    public CommandSpec getSpec() {
        return spec;
    }
}
