package io.github.fabiojose.snip.util;

import java.io.PrintStream;

/**
 * @author fabiojose
 */
public class Reporter {

    private PrintStream out;

    private Reporter() {}

    public static Reporter create() {

        var reporter = new Reporter();
        reporter.out = System.out;

        return reporter;
    }

    public void message(String message) {

        out.println();
        out.print(" > > ");
        out.print(message);
        out.println();
        out.println();

    }

    public void success(String message){
        message("[SUCCESS] " + message);
    }

    public void failure(String message){
        message("[FAIL] " + message);
    }
}
