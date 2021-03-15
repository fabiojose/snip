package io.github.kattlo.snip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Slf4j
public abstract class BaseCommand implements Runnable {
    
    static final String HOME_DIR = ".snip";
    static final String CONFIG_FILE_NAME = "config.yml";

    private static final String CONFIG_FILE_CONTENT = "/config.yml";

    private void write(String value, FileWriter out) {
        try{
            out.write(value);
            out.write(System.lineSeparator());
        }catch(IOException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void generateConfig(Path home) throws IOException {

        try(var in = getClass().getResourceAsStream(CONFIG_FILE_CONTENT);
            var out = new FileWriter(new File(home.toFile(), CONFIG_FILE_NAME))){


            new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines()
                .forEach(l -> write(l, out));

        }
    }

    private void verifyHome() throws IOException{
        var userHome = System.getProperty("user.home");
        log.debug("The user.home {}", userHome);

        var snipHome = Path.of(userHome, HOME_DIR);

        if(!snipHome.toFile().exists()){
            log.debug("Creating the snip home at {}", snipHome);

            if(!snipHome.toFile().mkdir()){
                throw new CommandLine.ExecutionException(getSpec().commandLine(),
                    "Unable to create snip home at " + snipHome);
            }

            generateConfig(snipHome);
        }
    }

    private void loadConfig() {

        var loader = new Yaml();
        
    }

    @Override
    public void run() {

        System.out.println("--------------------------------------");
        try {
            verifyHome();
            
        }catch (IOException e){
            throw new CommandLine.ExecutionException(getSpec().commandLine(),
                e.getMessage(), e);
        }
    }

    public abstract CommandSpec getSpec();
}
