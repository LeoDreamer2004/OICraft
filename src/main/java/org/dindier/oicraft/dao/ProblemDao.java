package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;

public interface ProblemDao {

    /**
     * Create a new problem into the database
     */
    Problem createProblem(Problem problem);

    /**
     * Get a problem by its ID
     *
     * @param id the ID of the problem
     * @return the problem if it exists, otherwise null
     */
    Problem getProblemById(int id);

    /**
     * Get all problems in the database
     *
     * @return a list of all problems in the database
     * If there is no problem, an empty list will be returned.
     */
    Iterable<Problem> getProblemList();

    /**
     * Get all sample I/O pairs of a problem
     *
     * @param id the ID of the problem
     * @return a list of all sample I/O pairs of the problem
     * If there is no sample I/O pair, an empty list will be returned.
     */
    Iterable<IOPair> getSamplesById(int id);

    /**
     * Get all test I/O pairs of a problem
     *
     * @param id the ID of the problem
     * @return a list of all test I/O pairs of the problem
     * If there is no test I/O pair, an empty list will be returned.
     */
    Iterable<IOPair> getTestsById(int id);

    /**
     * Update a problem in the database
     *
     * @param problem the problem to update
     * @return the updated problem
     */
    Problem updateProblem(Problem problem);

    /**
     * Delete a problem from the database
     *
     * @param problem the problem to delete
     */
    void deleteProblem(Problem problem);

    /**
     * Get the number of submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the number of submissions if the problem exists, otherwise null
     */
    Integer getSubmissionCount(int problemId);

    /**
     * Get the number of passed submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the number of passed submissions if the problem exists, otherwise null
     */
    Integer getPassedSubmissionCount(int problemId);
}
