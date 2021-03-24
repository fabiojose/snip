package io.github.kattlo.snip.templation;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zeroturnaround.exec.ProcessExecutor;

import io.github.kattlo.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class ScriptExecutor {

    private Path basedir;
    private List<String> linux;
    private List<String> windows;

    public void execute() {

        List<String> execute = List.of();
        if(IS_OS_LINUX || IS_OS_MAC) {
            log.debug("Executing linux commands {}", linux);
            execute = linux;
        }   

        if(IS_OS_WINDOWS) {
            log.debug("Executig windows commands {}", windows);
            execute = windows;
        }

        execute.forEach(command -> {
            log.debug("Trying to execute the command {}", command);

            try{
                new ProcessExecutor()
                    .directory(basedir.toFile())
                    .redirectOutput(System.out)
                    .redirectError(System.err)
                    .commandSplit(command)
                    .execute();

            }catch(IOException | InterruptedException | TimeoutException e){
                // TODO Use a reporter instead of sysout
                System.out.println("Failure to execute the command: " + command
                    + "\n message: " + e.getMessage());
            }
        });
    }

    public static ScriptExecutor create(JSONObject script, Path basedir) {
        var s = JSONUtil.pointer(script);

        var linux = s.asArray("#/linux")
            .map(JSONArray::toList)
            .orElseGet(() -> List.of())
            .stream()
            .map(Object::toString)
            .collect(Collectors.toList());

        var windows = s.asArray("#/windows")
            .map(JSONArray::toList)
            .orElseGet(() -> List.of())
            .stream()
            .map(Object::toString)
            .collect(Collectors.toList());

        var executor = new ScriptExecutor();
        executor.basedir = Objects.requireNonNull(basedir);

        executor.linux = linux;
        executor.windows = windows;

        return executor;
    }
}
