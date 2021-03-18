package io.github.kattlo.snip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.unix4j.Unix4j;
import org.unix4j.unix.sed.SedOption;

import io.github.kattlo.snip.context.Context;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "create",
    description = "To create app based on template",
    mixinStandardHelpOptions = true
)
@Slf4j
public class CreateCommand implements Runnable {

    public static final String APP_PARAM = "__s_app_";
    public static final String VERSION_PARAM = "__s_version_";
    public static final String NAMESPACE_PARAM = "__s_namespace_";

    private static final List<String> RESERVED_WORDS = List.of(
        APP_PARAM,
        VERSION_PARAM,
        NAMESPACE_PARAM
    );

    private static final String PLACEHOLDER_PATTERN_STRING = 
        "__[0-9a-zA-Z]+_[0-9a-zA-Z]+_";

    private static final Pattern PLACEHOLDER_PATTERN = 
        Pattern.compile("(" + PLACEHOLDER_PATTERN_STRING + ")");

    private static final Pattern OPTION_PATTERN = 
        Pattern.compile("^[\\w\\.\\-]+$");

    private static final Pattern PARAM_PATTERN = 
        Pattern.compile("^" + PLACEHOLDER_PATTERN_STRING + "=.+$");

    private static final Pattern LOCAL_TEMPLATE = 
        Pattern.compile("^file:/.+$");
    
    @Spec
    CommandSpec spec;

    private Path directory;
    private String appname;
    private String version;
    private String namespace;
    private File localTemplate;
    private URL remoteTemplate;

    private Map<String, String> parameterValues = new HashMap<>();

    private void validate(String value, String argName) {

        if(!OPTION_PATTERN.matcher(value).matches()){
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Invalid value of " + argName);
        }

    }

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
    public void setDirectory(Path directory) {
        if(directory.toFile().exists()){
            this.directory = directory;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Directory does not exists " + directory);
        }
    }

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
    public void setAppname(String appname) {
        validate(appname, "--app-name");
        this.appname = appname;

        parameterValues.put(APP_PARAM, this.appname);
    }

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
    public void setVersion(String version){
        validate(version, "--app-version");
        this.version = version;

        parameterValues.put(VERSION_PARAM, this.version);
    }

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
    public void setNamespace(String namespace){
        validate(namespace, "--app-namespace");
        this.namespace = namespace;

        parameterValues.put(NAMESPACE_PARAM, this.namespace);
    }

    @Option(
        names = {
            "-t",
            "--template"
        },
        description = "The template to create the app",
        descriptionKey = "template",
        required = true
    )
    public void setTemplate(URI template) {
        if(LOCAL_TEMPLATE.matcher(template.toString()).matches()){
            this.localTemplate = new File(template);
            if(!this.localTemplate.exists()){
                throw new CommandLine.ParameterException(spec.commandLine(),
                    "Local template not found at " + this.localTemplate);
            }
            log.debug("using template from local at: {}", this.localTemplate);
        } else {
            try {
                this.remoteTemplate = new URL("https://github.com/" + template + ".git");
                var https = (HttpsURLConnection)this.remoteTemplate.openConnection();
                https.setRequestMethod("HEAD");

                if(HttpsURLConnection.HTTP_OK != https.getResponseCode()){
                    throw new CommandLine.ParameterException(spec.commandLine(),
                        "Remote template not found at " + this.remoteTemplate);
                }

                log.debug("using template from remote at: {}", this.remoteTemplate);
            }catch(IOException e) {
                throw new CommandLine.ParameterException(spec.commandLine(),
                    "Unable to connect to remote template", e);
            }
        }
    }

    @Option(
        names = {
            "-p"
        },
        description = "Custom parameters used by the template",
        paramLabel = "__my_param_=my-value"
    )
    public void setParameters(String[] parameters) {
        var invalid = 
            Arrays.asList(parameters).stream()
                .filter(p -> !PARAM_PATTERN.matcher(p).matches())
                .collect(Collectors.toList());

        if(!invalid.isEmpty()){
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Some custom parameters are invalid " + invalid);
        }

        var reserved = Arrays.asList(parameters).stream()
            .map(p -> p.split("="))
            .map(p -> p[0])
            .filter(RESERVED_WORDS::contains)
            .collect(Collectors.toList());

        if(!reserved.isEmpty()){
            throw new CommandLine.ParameterException(spec.commandLine(),
                "Some custom parameters contains reserved words " + reserved);
        }

        var custom = 
            Arrays.asList(parameters).stream()
                .map(p -> p.split("="))
                .collect(Collectors.toMap((p) -> p[0], (p) -> p[1]));

        // add custom parameters to placeholders
        parameterValues.putAll(custom);
    }

    private Path checkout() throws IOException {

        Path target = null;

        if(null!= this.remoteTemplate){

        } else {
        
            target = Path.of("/tmp", "snip/" + this.localTemplate.getName());
            Files.createDirectories(target);

            FileUtils.copyDirectory(this.localTemplate, target.toFile(), false);
            log.debug("template copied to {}", target);
            
        }

        return target;
    }

    private void processNamespace(Path folder) {

        var dirtree = this.namespace.replaceAll("\\.", File.separator);
        log.debug("Directory tree from namespace {}", dirtree);

        var newFolder = Path.of(folder.toString().replaceAll(NAMESPACE_PARAM, dirtree));
        log.debug("New directory hierarchy to create {}", newFolder);

        try {
            FileUtils.moveDirectory(folder.toFile(), newFolder.toFile());
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processFolder(Map<String, String> ph, Path folder) {

        var matcher = PLACEHOLDER_PATTERN.matcher(folder.toString());
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

    private void processFolder(Context context) throws IOException {

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
                p2.toString().split(File.separator).length
                - p1.toString().split(File.separator).length
            )
            .peek(f -> log.debug("Folder to process {}", f))
            .collect(Collectors.toList());

        /*
         * this is necessary because during the processing
         * the folders may be moved
         */
        folders.forEach(folder -> {
            if(folder.toString().contains(NAMESPACE_PARAM)){
                processNamespace(folder);

            } else {

                var placeholders = context.getPlaceholders().entrySet().stream()
                    .filter(kv -> folder.toString().contains(kv.getKey()))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

                processFolder(placeholders, folder);

            }
        });
    }

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

    private void processFileName(Context context) throws IOException {

        Files.walk(context.getTarget())
            .filter(context.getInclude()::it)
            .filter(Files::isRegularFile)
            .filter(file -> 
                context.getPlaceholders().keySet().stream()
                    .filter(placeholder -> file.toString().contains(placeholder))
                    .findAny()
                    .isPresent()
            )
            .forEach(f -> 
                context.getPlaceholders().entrySet().stream()
                    .filter(kv -> f.toString().contains(kv.getKey()))
                    .forEach(placeholder -> 
                        processFileName(placeholder, f)
                    ));
    }

    private void processFileContent(Context context) throws IOException {

        Files.walk(context.getTarget())
            .filter(context.getInclude()::it)
            .filter(Files::isRegularFile)
            .peek(f -> log.debug("File to process its content {}", f.toAbsolutePath()))
            .forEach(f -> 
                context.getPlaceholders().entrySet().stream()
                    .peek(ph -> log.debug("Placeholder for file content {}", ph))
                    .forEach(ph -> {
                        try{
                            var tmp = new File(FileUtils.getTempDirectory(), f.toFile().getName());
                            FileUtils.copyFile(f.toFile(), tmp, false);

                            Unix4j
                                .cat(tmp)
                                .sed(SedOption.substitute, ph.getKey(), ph.getValue())
                                .toWriter(new FileWriter(f.toFile()));
                        }catch(IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
            );
    }

    @Override
    public void run() {
    
        try{
            // checkout to /tmp/snip/
            var template = checkout();

            // copy to --directory
            var appdir = Path.of(directory.toString(), appname);

            FileUtils.copyDirectory(template.toFile(), appdir.toFile());
            log.debug("template copied to app directory at {}", appdir);

            // remove .git folder (if exists)
            var gitdir = Path.of(appdir.toString(), ".git");
            FileUtils.deleteDirectory(gitdir.toFile());

            var context = Context.create(parameterValues, template, appdir);

            // process folders parameters
            processFolder(context);

            // process file name parameters
            processFileName(context);

            // process file content parameters
            processFileContent(context);

        }catch(IOException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }

    }
}
