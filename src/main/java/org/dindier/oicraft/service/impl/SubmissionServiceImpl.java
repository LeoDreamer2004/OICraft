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
    private ProblemServiceImpl problemService;

    private static final int POOL_SIZE = 16;
    private static final int WAITING_QUEUE_SIZE = 10000;

    private final ExecutorService executorService = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(WAITING_QUEUE_SIZE));

    private final List<Submission> waitingSubmissions = new CopyOnWriteArrayList<>();

    @Override
    public void getAIAdvice(Submission submission) {
        if (submission.getStatus() != Submission.Status.FAILED
                || submission.getAdviceAI() != null)
            return;
        log.info("Submission {} requested for AI advice", submission.getId());

        List<Checkpoint> checkpoints = submission.getCheckpoints();
        Checkpoint.Status status;
        // get the wrong info
        status = checkpoints.stream().filter(checkpoint -> checkpoint.getStatus() != Checkpoint.Status.AC &&
                checkpoint.getStatus() != Checkpoint.Status.P).findFirst().
                map(Checkpoint::getStatus).orElse(null);

        // build the question
        String prompt = null;
        if (status == null) {
            return;
        } else if (status.equals(Checkpoint.Status.WA)) {
            prompt = "下面这段代码的输出是错误的,请检查代码逻辑,并给出出现错误的原因";
        } else if (status.equals(Checkpoint.Status.TLE)) {
            prompt = "下面这段代码运行超时,请检查代码逻辑,并给出优化建议";
        } else if (status.equals(Checkpoint.Status.MLE)) {
            prompt = "下面这段代码运行内存超出限制,请检查代码逻辑,并给出优化建议";
        } else if (status.equals(Checkpoint.Status.RE)) {
            prompt = "下面这段代码运行出现运行时错误,请检查代码,并给出错误原因";
        } else if (status.equals(Checkpoint.Status.CE)) {
            prompt = "下面这段代码编译出现错误,请检查代码,并给出错误原因";
        }

        final String question = "这是一道编程问题，问题是这样的：" + "\n" +
                problemService.getProblemMarkdown(submission.getProblem()) +
                "\n" + prompt + "\n" + submission.getCode();

        try {
            executorService.execute(() -> {
                // get the advice from AI
                try {
                    if (waitingSubmissions.contains(submission))
                        return;
                    waitingSubmissions.add(submission);
                    String advice = aiAdapter.requestAI(question);
                    submission.setAdviceAI(advice);
                    submissionDao.updateSubmission(submission);
                    waitingSubmissions.remove(submission);
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

    @Autowired
    public void setProblemService(ProblemServiceImpl problemService) {
        this.problemService = problemService;
    }
}