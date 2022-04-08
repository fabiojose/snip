package io.github.fabiojose.snip.processor;

import io.github.fabiojose.snip.context.Context;

/**
 * @author fabiojose
 */
public interface Processor {

    void process(Context context);

    static Processor forDirectories() {
        return new DirectoryNameProcessor();
    }


    static Processor forFiles() {
        return new FileNameProcessor();
    }

    static Processor forContent() {
        return new FileContentProcessor();
    }
}
