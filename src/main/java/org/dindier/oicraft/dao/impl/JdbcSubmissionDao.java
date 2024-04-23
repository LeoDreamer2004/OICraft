package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("submissionDao")
public class JdbcSubmissionDao implements SubmissionDao {
    private SubmissionRepository submissionRepository;
    private ProblemRepository problemRepository;

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
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
                .orElse(null);
    }

    @Override
    public Iterable<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public Submission updateSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }
}
