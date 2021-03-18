package io.github.kattlo.snip;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;

import io.github.kattlo.snip.context.Context;
import io.github.kattlo.snip.processor.Processor;
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

    private static final List<String> RESERVED_WORDS = List.of(
        Context.APP_PARAM,
        Context.VERSION_PARAM,
        Context.NAMESPACE_PARAM
    );

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

        if(!Context.OPTION_PATTERN.matcher(value).matches()){
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

        parameterValues.put(Context.APP_PARAM, this.appname);
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

        parameterValues.put(Context.VERSION_PARAM, this.version);
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

        parameterValues.put(Context.NAMESPACE_PARAM, this.namespace);
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
                .filter(p -> !Context.PARAM_PATTERN.matcher(p).matches())
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
            Processor.forDirectories().process(context);;

            // process file name parameters
            Processor.forFiles().process(context);

            // process file content parameters
            Processor.forContent().process(context);

        }catch(IOException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }

    }
}
