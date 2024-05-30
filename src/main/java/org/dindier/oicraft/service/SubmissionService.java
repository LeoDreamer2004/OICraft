package org.dindier.oicraft.service;

import org.dindier.oicraft.assets.exception.EntityNotFoundException;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.springframework.lang.NonNull;

import java.util.List;

public interface SubmissionService {

    /**
     * Get the submission by id
     *
     * @param id The id of the submission
     * @return The submission
     * @throws EntityNotFoundException If the submission not exists
     */
    @NonNull
    Submission getSubmissionById(int id) throws EntityNotFoundException;

    /**
     * Get the submissions by a user of a problem with the given page number
     * If the user is null, then get all the submissions of the problem
     *
     * @param problem The problem requested
     * @param page The page number
     * @param user The user requested
     * @return The submissions got
     */
    List<Submission> getSubmissionsInPage(Problem problem, int page, User user);

    /**
     * Get the page number of the submission
     * If the user is null, then get all the submissions of the problem
     *
     * @param problem The problem requested
     * @param user The user requested
     * @return The pages of the submissions
     */
    int getSubmissionPages(Problem problem, User user);

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
     * @return The submission with the advice
     */
    Submission getAIAdvice(Submission submission);
}
