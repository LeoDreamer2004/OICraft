package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.constant.ConfigConstants;
import org.dindier.oicraft.assets.exception.NoAuthenticationError;
import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.assets.exception.EntityNotFoundException;
import org.dindier.oicraft.model.*;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.util.code.CodeChecker;
import org.dindier.oicraft.util.code.CodeCheckerFactory;
import org.dindier.oicraft.util.code.lang.Language;
import org.dindier.oicraft.util.code.lang.Status;
import org.dindier.oicraft.util.search.SearchHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service("problemService")
@Slf4j
public class ProblemServiceImpl implements ProblemService {
    private ProblemDao problemDao;
    private CheckpointDao checkpointDao;
    private SubmissionService submissionService;


    private final ExecutorService executorService = new ThreadPoolExecutor(
            ConfigConstants.TEST_POOL_SIZE,
            ConfigConstants.TEST_POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(ConfigConstants.WAITING_QUEUE_SIZE
            ));


    @NotNull
    @Override
    public Problem getProblemById(int id) throws EntityNotFoundException {
        Problem problem = problemDao.getProblemById(id);
        if (problem == null) {
            throw new EntityNotFoundException(Problem.class);
        }
        return problem;
    }

    @Override
    public Problem saveProblem(Problem problem) {
        if (problem.getId() == 0) {
            log.info("Created new problem: {}", problem.getTitle());
        } else {
            log.info("Save problem {}: {}", problem.getId(), problem.getTitle());
        }
        return problemDao.saveProblem(problem);
    }

    @Override
    public void deleteProblem(Problem problem) {
        log.info("Delete problem: {}", problem.getId());
        problemDao.deleteProblem(problem);
    }

    @Override
    public List<IOPair> getSamples(@NonNull Problem problem) {
        List<IOPair> ioPairs = problem.getIoPairs();
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.SAMPLE)).toList();
    }

    @Override
    public List<IOPair> getTests(@NonNull Problem problem) {
        List<IOPair> ioPairs = problem.getIoPairs();
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.TEST)).toList();
    }


    @Override
    public List<Problem> getTriedProblems(User user) {
        if (user == null) return List.of();
        return user.getSubmissions().stream().map(Submission::getProblem)
                .distinct().sorted(Comparator.comparingInt(Problem::getId)).toList();
    }

    @Override
    public List<Problem> getPassedProblems(User user) {
        if (user == null) return List.of();
        return user.getSubmissions().stream()
                .filter(submission -> submission.getResult() == Submission.Result.PASSED)
                .map(Submission::getProblem)
                .distinct().sorted(Comparator.comparingInt(Problem::getId)).toList();
    }

    @Override
    public List<Problem> getNotPassedProblems(User user) {
        return getTriedProblems(user).stream()
                .filter(problem -> !getPassedProblems(user).contains(problem))
                .toList();
    }

    @Override
    public int testCode(User user, Problem problem, String code, String language) {
        Submission temp = new Submission(user, problem, code,
                Language.fromString(language));
        final Submission submission = submissionService.saveSubmission(temp);
        final int id = submission.getId();
        final Iterable<IOPair> ioPairs = getTests(problem);

        try {
            executorService.execute(() -> {
                log.info("Start testing code for submission {}", id);

                int score = 0;
                boolean passed = true;

                Iterator<IOPair> iterator = ioPairs.iterator();
                CodeChecker codeChecker = CodeCheckerFactory.getCodeChecker();
                while (iterator.hasNext()) {
                    IOPair ioPair = iterator.next();
                    Checkpoint checkpoint = new Checkpoint(submission, ioPair);
                    checkpoint = checkpointDao.saveCheckpoint(checkpoint);

                    // test the code
                    try {
                        // do not delete the files until the last test
                        codeChecker.setIO(code, language, ioPair.getInput(), ioPair.getOutput())
                                .setLimit(problem.getTimeLimit(), problem.getMemoryLimit())
                                .test(!iterator.hasNext());
                    } catch (CodeCheckerError e) {
                        log.warn("CodeChecker encounter exception: {}", e.getMessage());
                        submission.setResult(Submission.Result.FAILED);
                        submissionService.saveSubmission(submission);
                    }

                    // read the result
                    checkpoint.setStatus(codeChecker.getStatus());
                    checkpoint.setUsedTime(codeChecker.getUsedTime());
                    checkpoint.setUsedMemory(codeChecker.getUsedMemory());
                    checkpoint.setInfo(codeChecker.getInfo());
                    checkpointDao.saveCheckpoint(checkpoint);
                    if (codeChecker.getStatus() == Status.AC) {
                        score += ioPair.getScore();
                    } else {
                        passed = false;
                    }
                }

                // update the submission
                submission.setScore(score);
                submission.setResult(passed ?
                        Submission.Result.PASSED : Submission.Result.FAILED);
                submissionService.saveSubmission(submission);
            });
        } catch (RejectedExecutionException e) {
            log.error("Executor rejected task for submission {}: {}", id, e.getMessage());
            submission.setResult(Submission.Result.WAITING);
            submissionService.saveSubmission(submission);
        }

        return id;
    }

    @Override
    public int hasPassed(User user, @NonNull Problem problem) {
        if (user == null) return 0;
        if (getPassedProblems(user).contains(problem)) return 1;
        if (!getTriedProblems(user).contains(problem)) return 0;
        return -1;
    }

    @Override
    public Map<Problem, Integer> getProblemPageWithPassInfo(User user, int page) {
        List<Problem> problems = problemDao.getProblemInRange((page - 1) * ConfigConstants.PROBLEMS_PER_PAGE, ConfigConstants.PROBLEMS_PER_PAGE);
        Map<Problem, Integer> map = new TreeMap<>();
        if (user == null) {
            for (Problem problem : problems) {
                map.put(problem, 0);
            }
            return map;
        }
        List<Problem> passedProblems = getPassedProblems(user);
        List<Problem> failedProblems = getNotPassedProblems(user);
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
    public int getProblemPages() {
        return (int) Math.ceil(problemDao.getProblemCount() / (double) ConfigConstants.PROBLEMS_PER_PAGE);
    }

    @Override
    public int getProblemPageNumber(int id) {
        int idx = problemDao.getProblemIndex(id);
        return (idx - 1) / ConfigConstants.PROBLEMS_PER_PAGE + 1;
    }

    @Override
    public String getProblemMarkdown(Problem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(problem.getTitle()).append("\n\n")
                .append("## 题目描述\n\n").append(problem.getDescription()).append("\n\n")
                .append("## 输入格式\n\n").append(problem.getInputFormat()).append("\n\n")
                .append("## 输出格式\n\n").append(problem.getOutputFormat()).append("\n\n")
                .append("## 样例\n\n");
        for (IOPair ioPair : getSamples(problem)) {
            sb.append("##### 输入\n\n").append("```\n")
                    .append(ioPair.getInput()).append(("\n```\n\n"))
                    .append("##### 输出\n\n").append("```\n")
                    .append(ioPair.getOutput()).append(("\n```\n\n"));
        }
        return sb.toString();
    }

    @Override
    public List<Problem> searchProblems(String keyword) {
        SearchHelper<Problem> helper = new SearchHelper<>();
        List<Problem> problems = new ArrayList<>();
        problemDao.getProblemList().forEach(problems::add);
        helper.setItems(problems);
        helper.setIdentifier("id", problem -> String.valueOf(problem.getId()));
        helper.addField("title", Problem::getTitle, 4.0f);
        return helper.search(keyword);
    }

    @Override
    public int getHistoryScore(User user, @NonNull Problem problem) {
        int score = 0;
        if (user == null) return 0;
        Iterable<Submission> submissions = user.getSubmissions();
        for (Submission submission : submissions) {
            if (submission.getProblemId() == problem.getId()) {
                score = Math.max(score, submission.getScore());
                if (submission.getResult().equals(Submission.Result.PASSED))
                    break;  // a simple optimization
            }
        }
        return score;
    }

    @Override
    public void checkCanEdit(User user, @NonNull Problem problem) throws NoAuthenticationError {
        if (user == null)
            throw new NoAuthenticationError("未登录");
        if (!user.isAdmin() && !user.equals(problem.getAuthor()))
            throw new NoAuthenticationError("仅作者或管理员可以编辑题目");
    }

    @Override
    public void checkCanEditCheckpoints(User user, @NonNull Problem problem) throws NoAuthenticationError {
        checkCanEdit(user, problem);
        if (!problem.getSubmissions().isEmpty())
            throw new NoAuthenticationError("已有提交记录，无法编辑测试点");
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
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
}
