package io.github.kattlo.snip.templation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.GsonBuilder;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class ConfigurationLoader {

    public static final String CONFIG_FILE_NAME = ".snip.yml";

    private static final String SCHEMA_FILE_PATH = "/snip.schema.json";

    private static Schema SCHEMA;
    private static Schema getSchema() {
        if(null== SCHEMA){
            try(var input = ConfigurationLoader.class.getResourceAsStream(SCHEMA_FILE_PATH)){

                var rawSchema = new JSONObject(new JSONTokener(input));

                SCHEMA = SchemaLoader.load(rawSchema);

                log.debug("Schema loaded: {}", SCHEMA_FILE_PATH);

            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }

        return SCHEMA;
    }

    private static Yaml YAML;
    private static Yaml getYamlLoader() {
        if(null== YAML){
            YAML = new Yaml();
        }

        return YAML;
    }
    
    public static Map<String, Object> loadAsMap(Path config) {
        Objects.requireNonNull(config);

        try {
            return getYamlLoader().load(new FileReader(config.toFile()));
        }catch(IOException e){
            throw new UncheckedIOException(e);
        } 
    }

    private static String toStringifiedJSON(Map<String, Object> yaml) {
        log.debug("Map to write as JSON: {}", yaml);

        var serializer = new GsonBuilder().create();
        var result = serializer.toJson(yaml);
        log.debug("Map as stringified json {}", result);

        return result;
    }

    private static JSONObject parseJson(String json) {

        return new JSONObject(new JSONTokener(json));

    }

    static Optional<JSONObject> loadFile(Path file) {
        log.debug("Try to load config from file {}", file);

        try {
            var map = loadAsMap(file);
            var jsonstr = toStringifiedJSON(map);

            var jsonobj = parseJson(jsonstr);
            var schema = getSchema();
            schema.validate(jsonobj);

            return Optional.of(jsonobj);

        }catch(UncheckedIOException e){
            if(e.getCause() instanceof FileNotFoundException){
                return Optional.empty();
            }
            throw e;
        }catch(ValidationException e) {
            // TODO Use a reporter instead of sysout
            System.err.println(e.getAllMessages());
            throw e;
        }

    }

    public static Optional<JSONObject> load(Path appdir) {
        if(!Files.isDirectory(appdir)){
            throw new IllegalArgumentException("its not directory " + appdir);
        }
        
        return loadFile(appdir.resolve(CONFIG_FILE_NAME));
    }
}
