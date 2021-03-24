package io.github.kattlo.snip.context;

/**
 * @author fabiojose
 */
public class IllegalPlaceholderValueException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IllegalPlaceholderValueException(String message) {
        super(message);
    }
}
