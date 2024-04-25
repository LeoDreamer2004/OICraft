package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;

public interface SubmissionDao {
    /**
     * Create a new submission in the database
     *
     * @param submission the submission to create
     * @return A {@code Submission} class with the submission's ID set.
     * @implNote This method should also update the corresponding problem's submit count and passed
     * count and add 10 points to the user's experience if the submission is passed.
     */
    Submission createSubmission(Submission submission);

    /**
     * Update a submission in the database
     *
     * @param submission the submission to update
     * @return A {@code Submission} class with updated information.
     * @implNote This method should also update the corresponding problem's passed count
     * and add 10 points to the user's experience if the submission is passed.
     */
    Submission updateSubmission(Submission submission);

    /**
     * Get a submission by ID
     *
     * @param id the ID of the submission
     * @return A {@code Submission} class if the submission exists, {@code null} otherwise.
     */
    Submission getSubmissionById(int id);

    /**
     * Get all submissions corresponding to a problem
     *
     * @param problemId the ID of the problem
     * @return A list of {@code Submission} classes that correspond to the problem.
     * If the problem has no submission, an empty list will be returned.
     */
    Iterable<Submission> getSubmissionsByProblemId(int problemId);

    /**
     * Get all submissions in the database
     *
     * @return A list of all {@code Submission} classes in the database.
     * If there is no submission, an empty list will be returned.
     */
    Iterable<Submission> getAllSubmissions();

    Iterable<Submission> getSubmissionsByUserId(int userId);
}
