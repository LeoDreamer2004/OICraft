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
import java.util.Objects;
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

    @Override
    public void getAIAdvice(Submission submission) {
        if (submission.getStatus() != Submission.Status.FAILED)
            return;
        log.info("Submission {} requested for AI advice", submission.getId());

        final List<Checkpoint> checkpoints = submission.getCheckpoints();
        try {
            executorService.execute(() -> {
                StringBuilder question = new StringBuilder();
                String status = "";
                String prompt = "";
                //get the wrong info
                for (Checkpoint checkpoint : checkpoints) {
                    if (checkpoint.getStatus() != Checkpoint.Status.AC && checkpoint.getStatus() != Checkpoint.Status.P) {
                        status = checkpoint.getStatus().toString();
                        break;
                    }
                }

                //build the question
                if (Objects.equals(status, "WA")) {
                    prompt = "下面这段代码的输出是错误的,请检查代码逻辑,并给出出现错误的原因";
                } else if (Objects.equals(status, "TLE")) {
                    prompt = "下面这段代码运行超时,请检查代码逻辑,并给出优化建议";
                } else if (Objects.equals(status, "MLE")) {
                    prompt = "下面这段代码运行内存超出限制,请检查代码逻辑,并给出优化建议";
                } else if (Objects.equals(status, "RE")) {
                    prompt = "下面这段代码运行出现运行时错误,请检查代码,并给出错误原因";
                } else if (Objects.equals(status, "CE")) {
                    prompt = "下面这段代码编译出现错误,请检查代码,并给出错误原因";
                }

                question.append("这是一道编程问题，问题描述是：");
                question.append("\n");
                question.append(submission.getProblem().getDescription());
                question.append("\n");
                question.append(prompt);
                question.append("\n");
                question.append(submission.getCode());

                //get the advice from AI
                try {
                    String advice = aiAdapter.requestAI(question.toString());
                    submission.setAdviceAI(advice);
                    submissionDao.updateSubmission(submission);
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
