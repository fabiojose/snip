package io.github.kattlo.snip.templation;

/**
 * @author fabiojose
 */
public class TemplationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public TemplationNotFoundException(String message) {
        super(message);
    }
}
