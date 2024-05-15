package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public interface ProblemService {
    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     * @return The submission id
     */
    int testCode(User user, Problem problem, String code, String language);

    /**
     * Return whether the user has passed the problem
     *
     * @return 1 if the user passed,
     * 0 if the user hasn't submitted,
     * -1 if the user hasn't passed
     */
    int hasPassed(User user, @NonNull Problem problem);

    /**
     * Return a string of the markdown file of the problem
     *
     * @param problem The problem to get the markdown from
     * @return The string of the markdown file
     */
    String getProblemMarkdown(Problem problem);

    /**
     * Return the highest score of the user's history submission
     *
     * @param user    The user to get the history score from
     * @param problem The problem to get the history score from
     * @return The highest score of the user's history submission
     */
    int getHistoryScore(User user, @NonNull Problem problem);

    /**
     * Return whether the user can edit the problem
     * @param user The user to check
     * @param problem  The problem to check
     * @return Whether the user can edit the problem
     */
    boolean canEdit(User user, @NonNull Problem problem);

    /**
     * Get all problems with a certain user's pass info
     *
     * @param user the user logged in
     * @return A map of problems and the user's pass info
     *         If the user not exists, return an empty map
     */
    Map<Problem, Integer> getAllProblemWithPassInfo(User user);

    /**
     * Get the list of problems by keyword
     *
     * @param keyword the keyword to search
     * @return The list of problems that contains the keyword
     *         It's better to order the list by the relevance of the keyword
     */
    List<Problem> searchProblems(String keyword);
}
