package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.*;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.util.CodeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {
    private SubmissionDao submissionDao;
    private ProblemDao problemDao;
    private CheckpointDao checkpointDao;
    private UserDao userDao;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);

    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     */
    @Override
    public int testCode(User user, Problem problem, String code, String language) {
        // TODO: Implement this method
        Submission submission = new Submission(user, problem, code,
                Submission.Language.fromString(language));
        submission = submissionDao.createSubmission(submission);
        int id = submission.getId();
        submission.load();

        final Submission finalSubmission = submission;
        final Iterable<IOPair> ioPairs = problemDao.getTestsById(problem.getId());
        for (IOPair ioPair : ioPairs) {
            ioPair.load();
        }

        executorService.execute(() -> {
            logger.info("Start testing code for submission {}", id);

            CodeChecker codeChecker = new CodeChecker();
            int score = 0;
            boolean ifPass = true;

            for (IOPair ioPair : ioPairs) {
                try {
                    codeChecker.setIO(code, language, ioPair.getInput(), ioPair.getOutput(), id)
                            .setLimit(problem.getTimeLimit(), problem.getMemoryLimit())
                            .test();
                } catch (Exception e) {
                    logger.warn("CodeChecker error: {}", e.getMessage());
                    finalSubmission.setStatus(Submission.Status.FAILED);
                    submissionDao.updateSubmission(finalSubmission);
                    return;
                }
                Checkpoint checkpoint = new Checkpoint(
                        finalSubmission,
                        ioPair,
                        Checkpoint.Status.fromString(codeChecker.getStatus()),
                        codeChecker.getUsedTime(), codeChecker.getUsedMemory(),
                        codeChecker.getInfo()
                );
                checkpointDao.createCheckpoint(checkpoint);

                if (codeChecker.getStatus().equals("AC")) {
                    score += ioPair.getScore();
                } else {
                    ifPass = false;
                }
            }

            finalSubmission.setScore(score);
            finalSubmission.setStatus(ifPass ?
                    Submission.Status.PASSED : Submission.Status.FAILED);
            submissionDao.updateSubmission(finalSubmission);
        });
        return id;
    }

    @Override
    public int hasPassed(User user, Problem problem) {
        if (user == null) return 0;
        if (userDao.getPassedProblemsByUserId(user.getId()).contains(problem)) return 1;
        if (!userDao.getTriedProblemsByUserId(user.getId()).contains(problem)) return 0;
        return -1;
    }

    @Override
    public Iterable<Problem> getPassedProblems(User user) {
        return userDao.getPassedProblemsByUserId(user.getId());
    }

    @Override
    public byte[] getProblemMarkdown(Problem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(problem.getTitle()).append("\n\n")
                .append("## 题目描述\n\n").append(problem.getDescription()).append("\n\n")
                .append("## 输入格式\n\n").append(problem.getInputFormat()).append("\n\n")
                .append("## 输出格式\n\n").append(problem.getOutputFormat()).append("\n\n");
        for (IOPair ioPair : problemDao.getTestsById(problem.getId())) {
            sb.append("## 样例\n\n")
                    .append("#### 输入\n\n").append("```\n")
                    .append(ioPair.getInput()).append(("\n```\n\n"))
                    .append("#### 输出\n\n").append("```\n")
                    .append(ioPair.getOutput()).append(("\n```\n\n"));
        }
        return sb.toString().getBytes();
    }

    @Override
    public int getHistoryScore(User user, Problem problem) {
        // TODO: Implement this method

        return 0;
    }

    @Autowired
    private void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    private void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    @Autowired
    private void setCheckpointDao(CheckpointDao checkpointDao) {
        this.checkpointDao = checkpointDao;
    }

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
