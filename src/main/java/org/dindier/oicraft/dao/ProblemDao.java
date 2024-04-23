package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;

public interface ProblemDao {

    /**
     * Create a new problem into the database
     */
    void createProblem(Problem problem);

    Problem getProblemById(int id);

    Iterable<Problem> getProblemList();

    Iterable<IOPair> getSamplesById(int id);

    Iterable<IOPair> getTestsById(int id);

    void updateProblem(Problem problem);

    void deleteProblem(Problem problem);
}
