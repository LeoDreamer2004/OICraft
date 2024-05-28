package org.dindier.oicraft.service;

import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface ProblemService {

    /**
     * Get the problem by its id
     *
     * @param id The id of the problem
     * @return The problem with the id
     */
    @Nullable
    Problem getProblemById(int id);

    /**
     * Save a problem to the database
     *
     * @param problem the problem to save
     * @return The saved problem
     */
    Problem saveProblem(Problem problem);

    /**
     * Delete a problem from the database
     *
     * @param problem the problem to delete
     */
    void deleteProblem(Problem problem);

    /**
     * Get all sample I/O pairs of a problem
     *
     * @param problem The problem
     * @return a list of all sample I/O pairs of the problem
     * If there is no sample I/O pair, an empty list will be returned.
     */
    List<IOPair> getSamples(Problem problem);

    /**
     * Get all test I/O pairs of a problem
     *
     * @param problem The problem
     * @return a list of all test I/O pairs of the problem
     * If there is no test I/O pair, an empty list will be returned.
     */
    List<IOPair> getTests(Problem problem);

    /**
     * Get problems that the user has tried
     *
     * @param user The user to get tried problems
     * @return The list of problems that the user has tried
     */
    List<Problem> getTriedProblems(User user);

    /**
     * Get problems that the user has passed
     *
     * @param user The user to get passed problems
     * @return The list of problems that the user has passed
     */
    List<Problem> getPassedProblems(User user);

    /**
     * Get problems that the user has not passed but has tried
     *
     * @param user The user to get not passed problems
     * @return The list of problems that the user has not passed
     */
    List<Problem> getNotPassedProblems(User user);

    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     *
     * @return The submission id
     */
    int testCode(User user, Problem problem, String code, String language);

    /**
     * Return whether the user has passed the problem
     *
     * @return 1 if the user passed,
     * 0 if the user hasn't submitted,
     * -1 if the user hasn't passed
     */
    int hasPassed(User user, @NonNull Problem problem);

    /**
     * Return a string of the markdown file of the problem
     *
     * @param problem The problem to get the markdown from
     * @return The string of the markdown file
     */
    String getProblemMarkdown(Problem problem);

    /**
     * Return the highest score of the user's history submission
     *
     * @param user    The user to get the history score from
     * @param problem The problem to get the history score from
     * @return The highest score of the user's history submission
     */
    int getHistoryScore(User user, @NonNull Problem problem);

    /**
     * Return whether the user can edit the problem
     *
     * @param user    The user to check
     * @param problem The problem to check
     * @return Whether the user can edit the problem
     */
    boolean canEdit(User user, @NonNull Problem problem);

    /**
     * Get a page of problems with a certain user's pass info
     *
     * @param user the user logged in
     * @param page the page number
     * @return A map of problems and the user's pass info
     */
    Map<Problem, Integer> getProblemPageWithPassInfo(User user, int page);

    /**
     * Get the number of pages of problems
     *
     * @return The number of pages of problems
     */
    int getProblemPages();

    /**
     * Get the page number of a problem
     *
     * @param id The requested problem id
     * @return The page number
     */
    int getProblemPageNumber(int id);

    /**
     * Get the list of problems by keyword
     *
     * @param keyword the keyword to search
     * @return The list of problems that contains the keyword
     * It's better to order the list by the relevance of the keyword
     */
    List<Problem> searchProblems(String keyword);
}
