package org.dindier.oicraft.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("submissionDao")
@Slf4j
public class JpaSubmissionDao implements SubmissionDao {
    private SubmissionRepository submissionRepository;

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Submission getSubmissionById(int id) {
        return submissionRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public List<Submission> getSubmissionsInRangeByProblemId(int problemId, int start, int count) {
        return submissionRepository.findSubmissionInRangeByProblemId(problemId, start, count);
    }

    @Override
    public List<Submission> getSubmissionsInRangeByProblemIdAndUserId(int problemId, int userId, int start, int count) {
        return submissionRepository.findSubmissionInRangeByProblemAndUserId(problemId, userId, start, count);
    }

    @Override
    public int countByProblemId(int problemId) {
        return submissionRepository.countByProblemId(problemId);
    }

    @Override
    public int countByProblemIdAndUserId(int problemId, int userId) {
        return submissionRepository.countByProblemAndUserId(problemId, userId);
    }
}
