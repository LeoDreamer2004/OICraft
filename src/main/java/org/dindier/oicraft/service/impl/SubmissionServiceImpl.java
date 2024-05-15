package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.util.ai.AIAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service("submissionService")
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {
    private SubmissionDao submissionDao;
    private AIAdapter aiAdapter;

    private static final int POOL_SIZE = 16;
    private static final int WAITING_QUEUE_SIZE = 10000;

    private final ExecutorService executorService = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(WAITING_QUEUE_SIZE));

    private final List<Submission> waitingSubmissions = new CopyOnWriteArrayList<>();

    private final String prompt = """
            你是一个信息竞赛专家，你要为错误代码提供错误分析修改建议。
            你收到的问题包含以下几个部分：
            1. 问题描述：一个信息竞赛的题目
            2. 错误代码：一段无法通过测试的代码
            3. 错误信息：测试平台对这段代码产生的错误信息，可能是 Compile Error, Runtime Error, Time Limit Exceeded, Memory Limit Exceeded, Wrong Answer 等。
            你的回答只应该包括错误分析和修改建议两部分。""";

    @Override
    public void getAIAdvice(Submission submission) {
        if (submission.getStatus() != Submission.Status.FAILED
                || submission.getAdviceAI() != null)
            return;
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
            return;
        }

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

        try {
            executorService.execute(() -> {
                // get the advice from AI
                try {
                    if (waitingSubmissions.contains(submission))
                        return;
                    waitingSubmissions.add(submission);
                    String advice = aiAdapter.requestAI(prompt, question);
                    submission.setAdviceAI(advice);
                    submissionDao.updateSubmission(submission);
                    waitingSubmissions.remove(submission);
                } catch (Exception e) {
                    log.error("Submission {} AI advice request failed: {}", submission.getId(),
                            e.getMessage());
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("Submission {} AI advice request rejected: {}", submission.getId(),
                    e.getMessage());
        }
    }

    @Autowired
    public void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    public void setAiAdapter(AIAdapter aiAdapter) {
        this.aiAdapter = aiAdapter;
    }
}