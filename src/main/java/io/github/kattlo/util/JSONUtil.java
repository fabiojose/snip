package io.github.kattlo.util;

import java.util.Objects;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class JSONUtil {

    public static Pointer pointer(JSONObject o) {
        return new Pointer(o);
    }

    public static class Pointer {

        private final JSONObject o;
        private Pointer(JSONObject o){
            this.o = Objects.requireNonNull(o);
        }

        public Optional<JSONArray> asArray(String query) {
            return Optional.ofNullable((JSONArray)o.optQuery(query));
        }

        public Optional<JSONObject> asObject(String query) {
            return Optional.ofNullable((JSONObject)o.optQuery(query));
        }

        public Optional<String> asString(String query) {
            return Optional.ofNullable((String)o.optQuery(query));
        }

        public Optional<Boolean> asBoolean(String query) {
            return Optional.ofNullable((Boolean)o.optQuery(query));
        }
    }
}
