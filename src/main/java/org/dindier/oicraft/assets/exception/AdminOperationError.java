package org.dindier.oicraft.assets.exception;

/**
 * The administrator operation error
 */
public class AdminOperationError extends NoAuthenticationError {
    public AdminOperationError() {
        super();
    }

    public AdminOperationError(String e) {
        super(e);
    }
}
