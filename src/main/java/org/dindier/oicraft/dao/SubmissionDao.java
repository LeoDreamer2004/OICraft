package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;

public interface SubmissionDao {

    /**
     * Create the submission
     */
    void createSubmission(Submission submission);

    /**
     * Get the submission by id
     */
    Submission getSubmissionById(int id);
}
