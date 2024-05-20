package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.service.UserService;
import org.dindier.oicraft.util.ai.AIAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service("submissionService")
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private UserService userService;
    private AIAdapter aiAdapter;

    private static final int RECORD_PER_PAGE = 20;

    private final List<Integer> waitingSubmissions = new CopyOnWriteArrayList<>();

    @Override
    public Submission getSubmissionById(int id) {
        return submissionDao.getSubmissionById(id);
    }

    @Override
    public List<Submission> getSubmissionsInPage(Problem problem, int page) {
        return submissionDao.getSubmissionsInRangeByProblemId(problem.getId(),
                (page - 1) * RECORD_PER_PAGE, RECORD_PER_PAGE);
    }

    @Override
    public int getSubmissionPages(Problem problem) {
        return (int) Math.ceil(submissionDao.countByProblemId(problem.getId()) / (double) RECORD_PER_PAGE);
    }

    @Override
    public Submission saveSubmission(Submission submission) {
        User user = submission.getUser();
        Problem problem = submission.getProblem();
        Submission historySubmission = getSubmissionById(submission.getId());

        boolean check = true;
        if (!submission.getStatus().equals(Submission.Status.PASSED)) {
            check = false;
        } else if (historySubmission != null && historySubmission.getStatus().equals(Submission.Status.PASSED)) {
            check = false;
        }
        if (historySubmission == null) {
            problem.setSubmit(problem.getSubmit() + 1);
        }
        if (check) {
            problem.setPassed(problem.getPassed() + 1);
            // add experience if the submission is passed and the user has not passed the problem
            if (!user.getSubmissions()
                    .stream()
                    .filter(s -> s.getStatus().equals(Submission.Status.PASSED))
                    .map(Submission::getProblemId)
                    .toList().
                    contains(submission.getProblemId())) {
                user.setExperience(user.getExperience() + 10);
                userService.updateUser(user);
            }
        }

        problemDao.saveProblem(problem);

        if (submission.getId() == 0) {
            log.info("New submission created");
        } else {
            log.info("Submission {} saved", submission.getId());
        }
        return submissionDao.saveSubmission(submission);
    }

    @Override
    public Submission getAIAdvice(Submission submission) {
        if (submission.getStatus() != Submission.Status.FAILED
                || submission.getAdviceAI() != null
                || waitingSubmissions.contains(submission.getId()))
            return submission;
        log.info("Submission {} requested for AI advice", submission.getId());

        List<Checkpoint> checkpoints = submission.getCheckpoints();
        Checkpoint.Status status;

        // get the wrong info
        status = checkpoints.stream()
                .filter(checkpoint -> checkpoint.getStatus() != Checkpoint.Status.AC &&
                        checkpoint.getStatus() != Checkpoint.Status.P)
                .findFirst()
                .map(Checkpoint::getStatus)
                .orElse(null);

        // build the question
        String errorMessage;
        if (status == null) {
            return submission;
        }

        waitingSubmissions.add(submission.getId());
        errorMessage = switch (status) {
            case WA -> "Wrong Answer";
            case TLE -> "Time Limit Exceeded";
            case MLE -> "Memory Limit Exceeded";
            case RE -> "Runtime Error";
            case CE -> "Compile Error";
            default -> "无";
        };

        final String question = String.format("""
                        1. 问题描述：
                        %s
                        2. 错误代码：
                        %s
                        3.错误信息：
                        %s""",
                submission.getProblem().getDescription(),
                submission.getCode(),
                errorMessage);

        // get the advice from AI
        try {
            String prompt = """
                    你是一个信息竞赛专家，你要为错误代码提供错误分析修改建议。
                    你收到的问题包含以下几个部分：
                    1. 问题描述：一个信息竞赛的题目
                    2. 错误代码：一段无法通过测试的代码
                    3. 错误信息：测试平台对这段代码产生的错误信息，可能是 Compile Error, Runtime Error, Time Limit Exceeded, Memory Limit Exceeded, Wrong Answer 等。
                    你的回答只应该包括错误分析和修改建议两部分。""";
            String advice = aiAdapter.requestAI(prompt, question);
            submission.setAdviceAI(advice);
        } catch (Exception e) {
            log.error("Submission {} AI advice request failed: {}", submission.getId(),
                    e.getMessage());
        }

        saveSubmission(submission);
        waitingSubmissions.remove(Integer.valueOf(submission.getId()));
        return submission;
    }

    @Autowired
    public void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    public void setAiAdapter(AIAdapter aiAdapter) {
        this.aiAdapter = aiAdapter;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }
}