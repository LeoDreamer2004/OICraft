package org.dindier.oicraft.assets.exception;

/**
 * User do not have the authentication to do something
 */
public class NoAuthenticationError extends RuntimeException {
    public NoAuthenticationError() {
        super();
    }

    public NoAuthenticationError(String e) {
        super(e);
    }
}
