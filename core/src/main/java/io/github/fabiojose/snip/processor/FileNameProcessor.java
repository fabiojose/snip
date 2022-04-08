package io.github.fabiojose.snip.processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import io.github.fabiojose.snip.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class FileNameProcessor implements Processor {

    FileNameProcessor(){}

    private void processFileName(Entry<String, String> ph, Path file){
        log.debug("Placeholder {} and file {}", ph, file);

        var newFile = Path.of(file.toString().replaceAll(ph.getKey(), ph.getValue()));
        log.debug("File will be renamed to {}", newFile);

        try {
            FileUtils.moveFile(file.toFile(), newFile.toFile());
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void process(Context context) {
        try{
          Files.walk(context.getTarget())
            .filter(context.getInclude()::it)
            .filter(Files::isRegularFile)
            .filter(file ->
                context.getPlaceholders().entries().keySet().stream()
                    .filter(placeholder -> file.toString().contains(placeholder))
                    .findAny()
                    .isPresent()
            )
            .peek(f -> log.debug("File to process {}", f))
            .forEach(f ->
                context.getPlaceholders().entries().entrySet().stream()
                    .filter(kv -> f.toString().contains(kv.getKey()))
                    .forEach(placeholder ->
                        processFileName(placeholder, f)
                    )
            );
        }catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
