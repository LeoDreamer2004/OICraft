package org.dindier.oicraft.assets.exception;

import org.dindier.oicraft.model.User;

/**
 * The requested user was not found
 */
public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException() {
        super(User.class);
    }

    public UserNotFoundException(String e) {
        super(User.class, e);
    }
}
