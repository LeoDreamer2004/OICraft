package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;

public interface ProblemDao {

    /**
     * Create a new problem into the database
     */
    Problem createProblem(Problem problem);

    Problem getProblemById(int id);

    Iterable<Problem> getProblemList();

    Iterable<IOPair> getSamplesById(int id);

    Iterable<IOPair> getTestsById(int id);

    Problem updateProblem(Problem problem);

    void deleteProblem(Problem problem);
}
