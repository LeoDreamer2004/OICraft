package org.dindier.oicraft.service;

public interface ProblemService {
    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     */
    int testCode(int problemId, String code, String language);
}
