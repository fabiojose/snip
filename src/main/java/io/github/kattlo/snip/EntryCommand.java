package io.github.kattlo.snip;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(
    name = "snip",
    mixinStandardHelpOptions = true,
    subcommands = {
        CreateCommand.class
    }
)
public class EntryCommand {

}
