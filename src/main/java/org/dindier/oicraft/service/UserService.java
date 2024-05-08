package org.dindier.oicraft.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.springframework.lang.Nullable;

public interface UserService {

    /**
     * Create a new user, with password encoded
     *
     * @param user The user to create
     * @return The created user object
     */
    User createUser(User user);

    /**
     * Get user by request
     *
     * @param request The request to get user from
     * @return The user from the request. If not authenticated, return null
     */
    @Nullable
    User getUserByRequest(HttpServletRequest request);

    /**
     * Encode the password
     *
     * @param password The password to encode
     * @return The encoded password
     */
    String encodePassword(String password);

    /**
     * Record the check-in of a user & add EXP etc.
     *
     * @param user The user to check in
     * @implNote Check-in is a daily task, so the user can only check in once a day
     */
    void checkIn(User user);

    /**
     * Check if the user has checked in today
     *
     * @param user The user to check
     * @return Whether the user has checked in today
     */
    boolean hasCheckedInToday(User user);

    /**
     * Send a verification code to the email
     *
     * @param user  The user to send the code to
     * @param email The email to send the code to
     */
    void sendVerificationCode(User user, String email);

    /**
     * Verify the email with the code. If true, record the email into the database
     *
     * @param user  The user to verify
     * @param email The email to verify
     * @param code  The verification code sent from the client
     * @return Whether the email is verified
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean verifyEmail(User user, String email, String code);

    /**
     * Save the avatar of the user
     * If avatar is null, the avatar will be deleted
     * @param user The user to save the avatar to
     * @return if the avatar is saved successfully
     */
    int saveUserAvatar(User user, byte[] avatar);

}
