package org.dindier.oicraft.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.springframework.lang.Nullable;

public interface UserService {

    /**
     * Create a new user, with password encoded
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
     * Record the check-in of a user & add EXP etc.
     *
     * @param user The user to check in
     * @implNote Check-in is a daily task, so the user can only check in once a day
     */
    void checkIn(User user);

    /**
     * Check if the user has checked in today
     * @param user The user to check
     * @return Whether the user has checked in today
     */
    boolean hasCheckedInToday(User user);
}
