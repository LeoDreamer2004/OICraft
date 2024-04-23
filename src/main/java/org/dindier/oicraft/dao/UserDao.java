package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;

import java.util.List;

public interface UserDao {
    /**
     * Create a new user in database
     *
     * @param user the user to create
     * @return A {@code User} class with the user's ID set.
     * @implNote The user's grade will be set to {@code User.Grade.BEGINNER}.
     */
    User createUser(User user);

    /**
     * Update user's information in database
     *
     * @param user the user to update
     * @return A {@code User} class with updated information.
     * @implNote The user's grade will be updated according to the number of problems that the user
     * has passed.
     */
    User updateUser(User user);

    /**
     * Get user by ID from database
     *
     * @param userId the user's ID to query
     * @return A {@code User} class if the user exist, {@code null} otherwise.
     */
    User getUserById(int userId);

    /**
     * Get user by username from database
     *
     * @param username the username
     * @return A {@code User} class if the user exists, {@code null} otherwise.
     */
    User getUserByUsername(String username);

    /**
     * Get all problems that a user has submitted
     *
     * @param userId the user's ID
     * @return A list of {@code Problem} classes that the user has submitted.
     * If the user has not submitted any problem, an empty list will be returned.
     */
    List<Problem> getTriedProblemsByUserId(int userId);

    /**
     * Get all problems that a user has passed
     *
     * @param userId the user's ID
     * @return A list of {@code Problem} classes that the user has passed.
     * If the user has not passed any problem, an empty list will be returned.
     */
    List<Problem> getPassedProblemsByUserId(int userId);

    /**
     * Get all problems that a user has not passed
     *
     * @param userId the user's ID
     * @return A list of {@code Problem} classes that the user has not passed.
     * If the user has passed all problems, an empty list will be returned.
     */
    List<Problem> getNotPassedProblemsByUserId(int userId);
}
