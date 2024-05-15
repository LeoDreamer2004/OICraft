package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;
import org.springframework.lang.Nullable;

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
}
