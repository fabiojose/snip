package io.github.kattlo.snip.context;

/**
 * @author fabiojose
 */
public class IllegalPlaceholderException extends RuntimeException {
    private static final long serialVersionUID = 1L;
   
    public IllegalPlaceholderException(String message) {
        super(message);
    }
}