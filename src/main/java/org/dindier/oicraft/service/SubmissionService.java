package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Submission;

public interface SubmissionService {

    /**
     * Get the submission by id
     *
     * @param id The id of the submission
     * @return The submission
     */
    Submission getSubmissionById(int id);

    /**
     * Create a new submission
     *
     * @param submission The submission to be saved
     * @return The submission saved
     * @implNote This method should also update the corresponding problem's submit count and passed
     * count and add 10 points to the user's experience if the submission is passed.
     */
    Submission saveSubmission(Submission submission);

    /**
     * Get the AI advice for the submission (which is a wrong answer)
     *   and then just save the advice to the database.
     * Use a thread to get the AI advice and do not block the procedure
     *
     * @param submission The submission to be given advice
     */
    void getAIAdvice(Submission submission);
}
