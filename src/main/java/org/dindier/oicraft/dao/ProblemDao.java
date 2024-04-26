package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;

import java.util.List;

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
    List<IOPair> getSamplesById(int id);

    /**
     * Get all test I/O pairs of a problem
     *
     * @param id the ID of the problem
     * @return a list of all test I/O pairs of the problem
     * If there is no test I/O pair, an empty list will be returned.
     */
    List<IOPair> getTestsById(int id);

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
     * @implNote This method will also delete all submissions and I/O pairs of the problem
     */
    void deleteProblem(Problem problem);

    /**
     * Get the list of submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the list of submissions for the problem,
     * if the problem not exists, return an empty list
     */
    List<Submission> getAllSubmissions(int problemId);

    /**
     * Get the list of passed submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the list of passed submissions for the problem,
     * if the problem not exists, return an empty list
     */
    List<Submission> getPassedSubmissions(int problemId);

    /**
     * Get the count of submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the count of submissions for the problem,
     * if the problem not exists, return 0
     */
    int getSubmissionCount(int problemId);

    /**
     * Get the count of passed submissions for a problem
     *
     * @param problemId the id of the problem
     * @return the count of passed submissions for the problem,
     * if the problem not exists, return 0
     */
    int getPassedSubmissionCount(int problemId);
}
