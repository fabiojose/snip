package io.github.kattlo.snip.processor;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.unix4j.Unix4j;
import org.unix4j.unix.sed.SedOption;

import io.github.kattlo.snip.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class FileContentProcessor implements Processor {

    FileContentProcessor(){}

    @Override
    public void process(Context context) {

        try{
            Files.walk(context.getTarget())
                .filter(context.getInclude()::it)
                .filter(Files::isRegularFile)
                .peek(f -> log.debug("File to process its content {}", f.toAbsolutePath()))
                .forEach(f -> {
                    try{
                        var tmpDir = new File(FileUtils.getTempDirectory(), context.getPlaceholders().getAppname());
                        FileUtils.forceMkdir(tmpDir);

                        var tmpFile = new File(tmpDir, UUID.randomUUID().toString());
                        FileUtils.copyFile(f.toFile(), tmpFile, false);

                        var command = Unix4j.fromFile(tmpFile);

                        context.getPlaceholders().entries().entrySet().stream()
                            .peek(ph -> log.debug("Placeholder for file content {}", ph))
                            .forEach(ph -> {
                                command.sed(SedOption.substitute, ph.getKey(), ph.getValue());
                            });

                        // delete file before the sed output
                        FileUtils.forceDelete(f.toFile());

                        command.toFile(f.toFile());

                    }catch(IOException e){
                        throw new UncheckedIOException(e);
                    }
                });

        }catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
