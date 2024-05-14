package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("submissionService")
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {
    private SubmissionDao submissionDao;

    @Override
    public void getAIAdvice(Submission submission) {
        if (submission.getStatus() != Submission.Status.FAILED)
            return;
        log.info("Submission {} requested for AI advice", submission.getId());
        // TODO: Implement this method
        // NOTICE: Use a thread to ask for advice, and return quickly
        // I'm sure you don't want the user waiting for the browser loading

        // temporary implementation
        submission.setAdviceAI("你好，我是科大讯飞，我不能为你提供任何帮助，非常抱歉");
        submissionDao.updateSubmission(submission);
    }

    @Autowired
    public void setSubmissionDao(SubmissionDao submissionDao){
        this.submissionDao = submissionDao;
    }
}
