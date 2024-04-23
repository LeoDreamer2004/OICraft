package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.util.CodeChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {
    private SubmissionDao submissionDao;
    private ProblemDao problemDao;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * Create the submission model and return its id first,
     * and then use the threading pool to test the code
     */
    @Override
    public int testCode(Problem problem, String code, String language) {
        // TODO: Implement this method
        Submission.Language lang = null;
        for (Submission.Language l : Submission.Language.values()) {
            if (l.name().equalsIgnoreCase(language)) {
                lang = l;
                break;
            }
        }
        Submission submission = new Submission(problem, code, lang);
        submissionDao.createSubmission(submission);
        int id = submission.getId();
        executorService.submit(() -> {
            Iterable<IOPair> ioPairs = problemDao.getTestsById(id);
            CodeChecker codeChecker = new CodeChecker();
            for(IOPair ioPair : ioPairs) {
                // codeChecker.setIO()
            }
        });
        return id;
    }

    @Override
    public int hasPassed(User user, Problem problem) {
        // TODO: Implement this method

        return 1;
    }

    @Override
    public Iterable<Problem> getPassedProblems(User user) {
        // TODO: Implement this method

        return null;
    }

    @Autowired
    private void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    private void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

}
