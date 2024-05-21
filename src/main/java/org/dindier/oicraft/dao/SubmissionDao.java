package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;
import org.springframework.lang.Nullable;

import java.util.List;

public interface SubmissionDao {
    /**
     * Save a submission in the database
     *
     * @param submission the submission to save
     * @return A saved {@code Submission} class
     */
    Submission saveSubmission(Submission submission);

    /**
     * Get a submission by ID
     *
     * @param id the ID of the submission
     * @return A {@code Submission} class if the submission exists, {@code null} otherwise.
     */
    @Nullable
    Submission getSubmissionById(int id);

    /**
     * Get all submissions in the database
     *
     * @return A list of all {@code Submission} classes in the database.
     * If there is no submission, an empty list will be returned.
     */
    @SuppressWarnings("unused")
    Iterable<Submission> getAllSubmissions();

    /**
     * Get all submissions in the database in a page by problem ID
     *
     * @param problemId the ID of the problem
     * @param start     the start index of the submissions
     * @param count     the number of submissions to get
     * @return A list of all {@code Submission} classes in the page.
     */
    List<Submission> getSubmissionsInRangeByProblemId(int problemId, int start, int count);

    /**
     * Get all submissions in the database in a page by problem ID and user ID
     *
     * @param problemId the ID of the problem
     * @param userId    the ID of the user
     * @param start     the start index of the submissions
     * @param count     the number of submissions to get
     * @return A list of all {@code Submission} classes in the page.
     */
    List<Submission> getSubmissionsInRangeByProblemIdAndUserId(int problemId, int userId, int start, int count);

    /**
     * Get the number of submissions by problem ID
     *
     * @param problemId the ID of the problem
     * @return The number of submissions
     */
    int countByProblemId(int problemId);

    /**
     * Get the number of submissions by problem ID and user ID
     *
     * @param problemId the ID of the problem
     * @param userId    the ID of the user
     * @return The number of submissions
     */
    int countByProblemIdAndUserId(int problemId, int userId);
}
