package io.github.fabiojose.snip.context;

/**
 * @author fabiojose
 */
public class ReservedPlaceholderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservedPlaceholderException(String message) {
        super(message);
    }
}
