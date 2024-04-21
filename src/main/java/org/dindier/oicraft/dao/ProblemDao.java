package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;

import java.util.List;

public interface ProblemDao {

    /**
     * Create a new problem into the database
     */
    void createProblem(Problem problem);

    Problem getProblemById(int id);

    Iterable<Problem> getProblemList();

    List<IOPair> getSamplesById(int id);

    List<IOPair> getTestsById(int id);


}
