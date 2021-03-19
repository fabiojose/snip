package io.github.kattlo.snip;

import io.github.kattlo.util.VersionUtil;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

/**
 * @author fabiojose
 */
@TopCommand
@CommandLine.Command(
    name = "snip",
    versionProvider = VersionUtil.QuarkusVersionProvider.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        CreateCommand.class
    }
)
public class EntryCommand {

}
