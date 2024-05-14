package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Submission;

public interface SubmissionService {
    /**
     * Get the AI advice for the submission (which is a wrong answer)
     *   and then just save the advice to the database.
     * Use a thread to get the AI advice and do not block the procedure
     * @param submission The submission to be given advice
     */
    void getAIAdvice(Submission submission);
}
