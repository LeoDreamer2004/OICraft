package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.dao.repository.UserRepository;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("submissionDao")
public class JpaSubmissionDao implements SubmissionDao {
    private SubmissionRepository submissionRepository;
    private ProblemRepository problemRepository;
    private JpaUserDao userDao;

    private final Logger logger = LoggerFactory.getLogger(JpaSubmissionDao.class);
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setUserDao(JpaUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Submission createSubmission(Submission submission) {
        problemRepository
                .findById(submission.getProblemId())
                .map(problem -> {
                    problem.setSubmit(problem.getSubmit() + 1);
                    return problem;
                })
                .map(problem -> {
                    ifAddExperience(submission);
                    ifAddPassed(submission, problem);
                    return problem;
                }).ifPresent(problem -> problemRepository.save(problem));
        Submission newSubmission = submissionRepository.save(submission);
        logger.info("Create submission for problem {} (id: {})", newSubmission.getProblemId(),
                newSubmission.getId());
        return newSubmission;
    }

    /*
     *Check if the submission is not in passed submissions
     */
    private void ifAddExperience(Submission submission) {
        if (submission.getStatus().equals(Submission.Status.PASSED) &&
                userRepository.findById(submission.getUser().getId())
                        .map(User::getSubmissions)
                        .map(submissions -> submissions
                                .stream()
                                .filter(s -> s.getStatus().equals(Submission.Status.PASSED))
                                .map(Submission::getProblemId)
                                .toList())
                        .map(problemIds -> !problemIds.contains(submission.getProblemId()))
                        .orElse(false)
        ) {
            userDao.addExperience(submission.getUser(), 10);
        }
    }

    private void ifAddPassed(Submission submission, Problem problem) {
        if (submission.getStatus().equals(Submission.Status.PASSED) &&
                !submissionRepository.findById(submission.getId())
                        .map(Submission::getStatus)
                        .map(status -> status.equals(Submission.Status.PASSED))
                        .orElse(false)) {
            problem.setPassed(problem.getPassed() + 1);
        }
    }

    @Override
    public Submission getSubmissionById(int id) {
        return submissionRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Submission> getSubmissionsByProblemId(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getSubmissions)
                .orElse(List.of());
    }

    @Override
    public Iterable<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public Submission updateSubmission(Submission submission) {
        Problem problem = submission.getProblem();

        ifAddExperience(submission);
        ifAddPassed(submission, problem);
        problemRepository.save(problem);

        Submission newSubmission = submissionRepository.save(submission);
        logger.info("Update submission for problem {} (id: {})", newSubmission.getProblemId(),
                newSubmission.getId());
        return newSubmission;
    }

    @Override
    public Iterable<Submission> getSubmissionsByUserId(int userId) {
        return userRepository.findById(userId)
                .map(User::getSubmissions)
                .orElse(List.of());
    }
}
