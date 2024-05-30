package org.dindier.oicraft.assets.exception;

/**
 * Exceptions happened on {@code CodeChecker}
 */
public class CodeCheckerError extends Exception {
    public CodeCheckerError() {
        super();
    }

    public CodeCheckerError(String e) {
        super(e);
    }

    public CodeCheckerError(Throwable cause) {
        super(cause);
    }
}
