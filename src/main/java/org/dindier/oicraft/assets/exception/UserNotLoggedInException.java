package org.dindier.oicraft.assets.exception;

/**
 * The user is not logged in
 */
public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException() {
        super("用户未登录");
    }
}
