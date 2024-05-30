package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.User;
import org.springframework.lang.Nullable;

public interface UserDao {
    /**
     * Save the user to database
     *
     * @param user the user to save
     * @return A {@code User} class with the user's ID set.
     */
    User saveUser(User user);

    /**
     * Get all users from database
     *
     * @return An iterable object of {@code User} classes.
     */
    Iterable<User> getAllUsers();

    /**
     * Delete a user from database
     *
     * @param id the user to delete
     * @implNote All submissions of the user will be deleted as well.
     */
    void deleteUser(int id);

    /**
     * Check if a user exists in database
     *
     * @param username the username to check
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    boolean existsUser(String username);

    /**
     * Get user by ID from database
     *
     * @param userId the user's ID to query
     * @return A {@code User} class if the user exist, {@code null} otherwise.
     */
    @Nullable
    User getUserById(int userId);

    /**
     * Get user by username from database
     *
     * @param username the username
     * @return A {@code User} class if the user exists, {@code null} otherwise.
     */
    @Nullable
    User getUserByUsername(String username);
}
