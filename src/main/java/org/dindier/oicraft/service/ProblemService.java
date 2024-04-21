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
     * Tips: get all the submissions of the user and the problem,
     * and check whether there is a submission that has passed
     */
    boolean hasPassed(User user, Problem problem);
}
