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

import java.io.IOException;
import java.util.Iterator;
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
        Submission temp = new Submission(user, problem, code,
                Submission.Language.fromString(language));
        int id = temp.getId();
        final Submission submission = submissionDao.createSubmission(temp);
        final Iterable<IOPair> ioPairs = problemDao.getTestsById(problem.getId());

        executorService.execute(() -> {
            logger.info("Start testing code for submission {}", id);

            CodeChecker codeChecker = new CodeChecker();
            int score = 0;
            boolean passed = true;

            Iterator<IOPair> iterator = ioPairs.iterator();
            while (iterator.hasNext()) {
                IOPair ioPair = iterator.next();
                Checkpoint checkpoint = new Checkpoint(submission, ioPair);
                checkpoint = checkpointDao.createCheckpoint(checkpoint);

                // test the code
                try {
                    codeChecker.setIO(code, language, ioPair.getInput(), ioPair.getOutput(), id)
                            .setLimit(problem.getTimeLimit(), problem.getMemoryLimit())
                            .test(!iterator.hasNext());
                } catch (IOException | InterruptedException e) {
                    logger.warn("CodeChecker encounter exception: {}", e.getMessage());
                    submission.setStatus(Submission.Status.FAILED);
                    submissionDao.updateSubmission(submission);
                }

                // read the result
                checkpoint.setStatus(Checkpoint.Status.fromString(codeChecker.getStatus()));
                checkpoint.setUsedTime(codeChecker.getUsedTime());
                checkpoint.setUsedMemory(codeChecker.getUsedMemory());
                checkpoint.setInfo(codeChecker.getInfo());
                checkpointDao.updateCheckpoint(checkpoint);
                if (codeChecker.getStatus().equals("AC")) {
                    score += ioPair.getScore();
                } else {
                    passed = false;
                }
            }

            // update the submission
            submission.setScore(score);
            submission.setStatus(passed ?
                    Submission.Status.PASSED : Submission.Status.FAILED);
            submissionDao.updateSubmission(submission);
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

    @Override
    public byte[] getProblemMarkdown(Problem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(problem.getTitle()).append("\n\n")
                .append("## 题目描述\n\n").append(problem.getDescription()).append("\n\n")
                .append("## 输入格式\n\n").append(problem.getInputFormat()).append("\n\n")
                .append("## 输出格式\n\n").append(problem.getOutputFormat()).append("\n\n")
                .append("## 样例\n\n");
        for (IOPair ioPair : problemDao.getSamplesById(problem.getId())) {
            sb.append("##### 输入\n\n").append("```\n")
                    .append(ioPair.getInput()).append(("\n```\n\n"))
                    .append("##### 输出\n\n").append("```\n")
                    .append(ioPair.getOutput()).append(("\n```\n\n"));
        }
        return sb.toString().getBytes();
    }

    @Override
    public int getHistoryScore(User user, Problem problem) {
        int score = 0;
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
}
