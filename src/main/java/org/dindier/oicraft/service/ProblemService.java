package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;

public interface ProblemService {
    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     */
    int testCode(Problem problem, String code, String language);

    /**
     * Return whether the user has passed the problem
     * @return 1 if the user passed,
     * 0 if the user hasn't submitted,
     * -1 if the user hasn't passed
     */
    int hasPassed(User user, Problem problem);

    /**
     * Return the problems that the user has passed
     */
    Iterable<Problem> getPassedProblems(User user);
}
