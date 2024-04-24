package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("submissionDao")
public class JdbcSubmissionDao implements SubmissionDao {
    private SubmissionRepository submissionRepository;
    private ProblemRepository problemRepository;
    private ProblemDao problemDao;

    private final Logger logger = LoggerFactory.getLogger(JdbcSubmissionDao.class);

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
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
                    if (submission.getStatus().equals(Submission.Status.PASSED)) {
                        problem.setPassed(problem.getPassed() + 1);
                    }
                    return problem;
                })
                .ifPresent(oldProblem -> problemRepository.save(oldProblem));
        Submission newSubmission = submissionRepository.save(submission);
        logger.info("Create submission for problem {} (id: {})", newSubmission.getProblemId(),
                newSubmission.getId());
        return newSubmission;
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
        problemRepository
                .findById(submission.getProblemId())
                .map(problem -> {
                    if (submission.getStatus().equals(Submission.Status.PASSED) &&
                            // Check if the submission is not in the passed submissions
                            problemDao
                                    .getPassedSubmissions(problem.getId())
                                    .stream()
                                    .map(Submission::getId)
                                    .noneMatch(id -> id == submission.getId())
                    ) {
                        problem.setPassed(problem.getPassed() + 1);
                    }
                    return problem;
                })
                .ifPresent(oldProblem -> problemRepository.save(oldProblem));
        Submission newSubmission = submissionRepository.save(submission);
        logger.info("Update submission for problem {} (id: {})", newSubmission.getProblemId(),
                newSubmission.getId());
        return newSubmission;
    }
}
