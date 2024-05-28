package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Problem;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProblemDao {

    /**
     * save a problem to the database
     *
     * @param problem the problem to save
     */
    Problem saveProblem(Problem problem);

    /**
     * Get a problem by its ID
     *
     * @param id the ID of the problem
     * @return the problem if it exists, otherwise null
     */
    @Nullable
    Problem getProblemById(int id);

    /**
     * Get all problems in the database
     *
     * @return a list of all problems in the database
     * If there is no problem, an empty list will be returned.
     */
    Iterable<Problem> getProblemList();

    /**
     * Delete a problem from the database
     *
     * @param problem the problem to delete
     * @implNote This method will also delete all submissions and I/O pairs of the problem
     */
    void deleteProblem(Problem problem);

    /**
     * Get the count of problems in the database
     *
     * @return the count of problems in the database
     */
    int getProblemCount();

    /**
     * Find problems in a range
     *
     * @param start the start index
     * @param count the count of problems to find
     * @return a list of problems in the range
     */
    List<Problem> getProblemInRange(int start, int count);

    /**
     * Get the index of a problem
     *
     * @param problemId the ID of the problem
     * @return the index of the problem
     */
    int getProblemIndex(int problemId);
}
