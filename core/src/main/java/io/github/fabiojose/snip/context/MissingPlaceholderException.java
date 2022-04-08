package io.github.fabiojose.snip.context;

/**
 * @author fabiojose
 */
public class MissingPlaceholderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MissingPlaceholderException(String message) {
        super(message);
    }
}
