package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;

import java.util.List;

public interface SubmissionService {

    /**
     * Get the submission by id
     *
     * @param id The id of the submission
     * @return The submission
     */
    Submission getSubmissionById(int id);

    /**
     * Get the submissions of a problem with the given page number to show to the user
     *
     * @param problem The problem requested
     * @param page The page number
     * @return The submissions got
     */
    List<Submission> getSubmissionsInPage(Problem problem, int page);

    /**
     * Get the page number of the submission
     *
     * @param problem The problem requested
     * @return The pages of the submissions
     */
    int getSubmissionPages(Problem problem);

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
     * and then just save the advice to the database.
     * Use a thread to get the AI advice and do not block the procedure
     *
     * @param submission The submission to be given advice
     */
    void getAIAdvice(Submission submission);
}
