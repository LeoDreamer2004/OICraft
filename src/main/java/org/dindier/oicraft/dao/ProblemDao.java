package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import java.util.List;

public interface ProblemDao {

    /**
     * Create a new problem into the database
     */
    void createProblem(Problem problem);

    /**
     * Get the problem by id
     */
    Problem getProblemById(int id);

    /**
     * Get the list of problems
     */
    List<Problem> getProblems();

    /**
     * Get the samples of the problem by id
     */
    List<IOPair> getSamplesById(int id);

    /**
     * Get the tests of the problem by id
     */
    List<IOPair> getTestsById(int id);
}
