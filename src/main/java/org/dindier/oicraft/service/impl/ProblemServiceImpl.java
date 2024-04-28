package org.dindier.oicraft.service.impl;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
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
import java.io.UncheckedIOException;
import java.util.*;
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
    private static final int MAX_SEARCH_RESULT = 100;
    private static final Map<String, Float> boosts = new HashMap<>();

    static {
        boosts.put("title", 4.0f);
        boosts.put("description", 1.0f);
    }

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
    public List<Problem> searchProblems(String keyword) {
        // TODO: Implement this method
        List<Problem> result = new ArrayList<>();
        try (Directory directory = new ByteBuffersDirectory();
             IndexWriter indexWriter = new IndexWriter(directory,
                     new IndexWriterConfig(new SmartChineseAnalyzer()))
        ) {
            Iterable<Problem> problems = problemDao.getProblemList();
            for (Problem problem : problems) {
                Document document = new Document();
                document.add(new StringField("id", String.valueOf(problem.getId()), Field.Store.YES));
                document.add(new TextField("title", problem.getTitle(), Field.Store.YES));
                document.add(new TextField("description", problem.getDescription(), Field.Store.YES));
                indexWriter.addDocument(document);
            }
            indexWriter.commit();
            indexWriter.close();

            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title",
                    "description"}, new SmartChineseAnalyzer(), boosts);
            Query query;
            try {
                query = parser.parse(keyword);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            try (IndexReader indexReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                TopDocs topDocs = indexSearcher.search(query, MAX_SEARCH_RESULT);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    int id = Integer.parseInt(indexSearcher.doc(scoreDoc.doc).get("id"));
                    result.add(problemDao.getProblemById(id));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
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
