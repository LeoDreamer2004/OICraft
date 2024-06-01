package org.dindier.oicraft.assets.exception;

/**
 * Email verification error
 */
public class EmailVerificationError extends Exception {
    public EmailVerificationError() {
        super();
    }

    public EmailVerificationError(String e) {
        super(e);
    }
}
