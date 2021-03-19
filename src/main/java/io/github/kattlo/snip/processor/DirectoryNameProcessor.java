package io.github.kattlo.snip.processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import io.github.kattlo.snip.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class DirectoryNameProcessor implements Processor {

    private static final String UNIX_FILE_SEPARATOR = "/";

    DirectoryNameProcessor(){}

    private void processNamespace(Path folder, Context ctx) {

        var dirtree = ctx.getNamespace().replaceAll("\\.", UNIX_FILE_SEPARATOR);
        log.debug("Directory tree from namespace {}", dirtree);

        var newFolder = Path.of(folder.toString().replaceAll(Context.NAMESPACE_PARAM, dirtree));
        log.debug("New directory hierarchy to create {}", newFolder);

        try {
            FileUtils.moveDirectory(folder.toFile(), newFolder.toFile());
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processFolder(Map<String, String> ph, Path folder) {

        var matcher = Context.PLACEHOLDER_PATTERN.matcher(folder.toString());
        var found = "";
        while(matcher.find()){
            found = matcher.group(matcher.groupCount());
            log.debug("Latest placeholder within {}: {}", folder, found);
        }

        final var latest = found;

        var value = Optional.ofNullable(ph.get(latest));
        log.debug("Value to apply {} to {}", value, latest);

        value.ifPresent(v -> {
            var target = Path.of(folder.toString().replaceAll(latest, v));
            log.debug("New directory hierarchy to create {}", target);

            try{
                FileUtils.moveDirectory(folder.toFile(), target.toFile());
            }catch(IOException e){
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public void process(Context context) {

        try {
            var folders = Files.walk(context.getTarget())
                .filter(Files::isDirectory)
                .filter(context.getInclude()::folder)
                .filter(folder -> 
                    context.getPlaceholders().keySet().stream()
                        .filter(placeholder -> folder.toString().contains(placeholder))
                        .findAny()
                        .isPresent()
                )
                .sorted((p1, p2) -> 
                    // deepest paths first
                    FilenameUtils.separatorsToUnix(
                        p2.toString()).split(UNIX_FILE_SEPARATOR).length
                    - FilenameUtils.separatorsToUnix(
                        p1.toString()).split(UNIX_FILE_SEPARATOR).length
                )
                .peek(f -> log.debug("Folder to process {}", f))
                .collect(Collectors.toList());

           /*
            * this is necessary because during the processing
            * the folders may be moved
            */
            folders.forEach(folder -> {
                if(folder.toString().contains(Context.NAMESPACE_PARAM)){
                    processNamespace(folder, context);

                } else {

                    var placeholders = context.getPlaceholders().entrySet().stream()
                        .filter(kv -> folder.toString().contains(kv.getKey()))
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

                    processFolder(placeholders, folder);

                }
            });
        }catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
