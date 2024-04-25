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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    public byte[] getProblemMarkdown(Problem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(problem.getTitle()).append("\n\n")
                .append("## 题目描述\n\n").append(problem.getDescription()).append("\n\n")
                .append("## 输入格式\n\n").append(problem.getInputFormat()).append("\n\n")
                .append("## 输出格式\n\n").append(problem.getOutputFormat()).append("\n\n");
        int sampleCount = 0;
        for (IOPair ioPair : problemDao.getSamplesById(problem.getId())) {
            if (sampleCount++ == 0) {
                sb.append("## 样例\n\n");
            }
            sb.append("#### 样例").append(sampleCount).append("\n\n")
                    .append("##### 输入\n\n").append("```\n")
                    .append(ioPair.getInput()).append(("\n```\n\n"))
                    .append("##### 输出\n\n").append("```\n")
                    .append(ioPair.getOutput()).append(("\n```\n\n"));
        }
        return sb.toString().getBytes();
    }

    @Override
    public int getHistoryScore(User user, Problem problem) {
        int score = -1;
        if (user == null) return 0;
        Iterable<Submission> submissions = submissionDao.getSubmissionsByUserId(user.getId());
        for (Submission submission : submissions) {
            if (submission.getProblemId() == problem.getId()) {
                score = Math.max(score, submission.getScore());
            }
        }
        return score;
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

    @Override
    public Map<Problem, Integer> getAllProblemWithPassInfo(User user) {
        if (user == null) {
            TreeMap<Problem, Integer> map = new TreeMap<>();
            for (Problem problem : problemDao.getProblemList()) {
                map.put(problem, 0);
            }
            return map;
        }

        List<Problem> passedProblems = userDao.getPassedProblemsByUserId(user.getId());
        List<Problem> failedProblems = userDao.getNotPassedProblemsByUserId(user.getId());
        Iterable<Problem> problems = problemDao.getProblemList();
        Map<Problem, Integer> map = new TreeMap<>();
        for (Problem problem : problems) {
            if (passedProblems.contains(problem)) {
                map.put(problem, 1);
            } else if (failedProblems.contains(problem)) {
                map.put(problem, -1);
            } else {
                map.put(problem, 0);
            }
        }
        return map;
    }
}
