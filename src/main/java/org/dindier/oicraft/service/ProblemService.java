package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;

import java.io.InputStream;

public interface ProblemService {
    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     */
    int testCode(User user, Problem problem, String code, String language);

    /**
     * Return whether the user has passed the problem
     * @return 1 if the user passed,
     * 0 if the user hasn't submitted,
     * -1 if the user hasn't passed
     */
    int hasPassed(User user, Problem problem);

    /**
     * Return the problems that the user has passed
     * @param user The user to get the problems from
     * @return The problems that the user has passed
     */
    Iterable<Problem> getPassedProblems(User user);

    /**
     * Return a byte array of the markdown file of the problem
     * @param problem The problem to get the markdown from
     * @return The byte array of the markdown file
     */
    byte[] getProblemMarkdown(Problem problem);

    /**
     * Return the highest score of the user's history submission
     * @param user The user to get the history score from
     * @param problem The problem to get the history score from
     * @return The highest score of the user's history submission
     */
    int getHistoryScore(User user, Problem problem);
}
