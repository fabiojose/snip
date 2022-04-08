package io.github.fabiojose.snip.processor;

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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import io.github.fabiojose.snip.context.Context;
import io.github.fabiojose.snip.context.Placeholders;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class DirectoryNameProcessor implements Processor {

    private static final String UNIX_FILE_SEPARATOR = "/";

    DirectoryNameProcessor(){}

    private void processNamespace(Path folder, Context ctx) {

        var dirtree = ctx.getPlaceholders().getNamespace()
            .replaceAll("\\.", UNIX_FILE_SEPARATOR);

        log.debug("Namespace: Directory tree {}", dirtree);

        var target = Path.of(folder.toString().replaceAll(Context.NAMESPACE_PARAM, dirtree));

        try {
            if(!Files.exists(target)){
                log.debug("Namespace: new directory hierarchy to create {}", target);
                    FileUtils.moveDirectory(folder.toFile(), target.toFile());
            } else {
                log.debug("Namespace: directory already exists (copy the content) {}",
                    target);

                // move resources to existing target
                FileUtils.listFiles(folder.toFile(), FileFileFilter.FILE,
                        DirectoryFileFilter.DIRECTORY)
                    .forEach(f -> {
                        try{
                            if(f.isDirectory()){
                                FileUtils.moveDirectoryToDirectory(f, target.toFile(), false);
                            } else {
                                FileUtils.moveFileToDirectory(f, target.toFile(), false);
                            }
                        }catch(IOException e){
                            throw new UncheckedIOException(e);
                        }
                    });

                // delete source folder, if empty
                if(FileUtils.listFiles(folder.toFile(),
                    FileFileFilter.FILE,
                    DirectoryFileFilter.DIRECTORY)
                        .isEmpty()){

                    FileUtils.deleteDirectory(folder.toFile());
                    log.debug("Empty source directory deleted {}", folder);

                }
            }
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processFolder(Map<String, String> ph, Path folder) {

        var matcher = Placeholders.PLACEHOLDER_PATTERN.matcher(folder.toString());
        var found = "";
        while(matcher.find()){
            found = matcher.group(matcher.groupCount());
        }
        log.debug("Latest placeholder within {}: {}", folder, found);

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
                    context.getPlaceholders().entries().keySet().stream()
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

                    var placeholders = context.getPlaceholders().entries().entrySet().stream()
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
