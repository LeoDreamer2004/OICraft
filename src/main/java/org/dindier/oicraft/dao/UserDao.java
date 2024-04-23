package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.User;

public interface UserDao {
    User createUser(User user);
    User updateUser(User user);

    /**
     * Get user by ID from database
     *
     * @param id the user's ID to query
     * @return A {@code User} class if the user exist, {@code null} otherwise.
     */
    User getUserById(int id);

    /**
     * Get user by username from database
     *
     * @param username the username
     * @return A {@code User} class if the user exists, {@code null} otherwise.
     */
    User getUserByUsername(String username);
}
