package io.github.kattlo.snip.context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.kattlo.snip.templation.ConfigurationLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Getter
@Slf4j
public class Include {

    static final List<String> ALWAYS_IGNORE_FOLDER = List.of(
        ".git/"
    );

    static final List<String> ALWAYS_IGNORE_FILE = List.of(
        Context.SNIP_IGNORE,
        ConfigurationLoader.CONFIG_FILE_NAME
    );

    private List<String> all;
    private List<String> folders;
    private List<String> wildcards;
    private List<String> files;

    private Include(){
    }

    public boolean it(Path resource) {
        var included = folder(resource);

        if(included){
            included = wildcard(resource);
        }

        if(included){
            included = file(resource);
        }

        return included;
    }

    public boolean folder(Path folder) {

        var stream = folders.stream();

        if(Files.isDirectory(folder)){
            stream = folders.stream()
                .map(f -> f.substring(0, f.length() -1))
                .peek(f -> log.debug("Directory to ignore {}", f));
        }

        return !stream.filter(f -> folder.toString().replaceAll("\\", "/").contains(f))
                .findFirst()
                .isPresent();
    }

    public boolean wildcard(Path resource) {

        return !wildcards.stream()
            .map(w -> w.substring(1))
            .filter(w -> resource.toString().endsWith(w))
            .peek(w -> log.debug("Wildcard to ignore {}", w))
            .findAny()
            .isPresent();
    }

    public boolean file(Path file) {

        return !files.stream()
            .filter(f -> file.toString().contains(f))
            .peek(f -> log.debug("File to ignore {}", f))
            .findFirst()
            .isPresent();
    }

    public static Include empty() {

        var result = new Include();
        result.all = List.of();
        result.folders = List.of();
        result.wildcards = List.of();
        result.files = List.of();

        return result;
    }

    public static Include create(Path ignore) {

        if(Files.exists(ignore)){
            try(var in = new BufferedReader(new FileReader(ignore.toFile()))){

                var result = new Include();

                result.all = Collections.unmodifiableList(
                    in.lines()
                        .filter(l -> !(l.startsWith("#")))
                        .map(String::trim)
                        .filter(l -> !(l.isBlank()))
                        .collect(Collectors.toList())
                );

                result.folders = result.all.stream()
                        .filter(l -> l.endsWith("/"))
                        .collect(Collectors.toList());
                result.folders.addAll(ALWAYS_IGNORE_FOLDER);
                result.folders = Collections.unmodifiableList(result.folders);

                result.wildcards = Collections.unmodifiableList(
                    result.all.stream()
                        .filter(l -> l.startsWith("*."))
                        .collect(Collectors.toList())
                );

                result.files = result.all.stream()
                        .filter(l -> !(result.folders.contains(l)))
                        .filter(l -> !(result.wildcards.contains(l)))
                        .collect(Collectors.toList());

                result.files.addAll(ALWAYS_IGNORE_FILE);
                result.files = Collections.unmodifiableList(result.files);

                return result;

            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }

        return empty();
    }
}
