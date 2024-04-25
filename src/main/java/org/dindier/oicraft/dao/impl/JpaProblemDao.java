package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("problemDao")
public class JpaProblemDao implements ProblemDao {
    private ProblemRepository problemRepository;
    private final Logger logger = LoggerFactory.getLogger(JpaProblemDao.class);

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    public Problem createProblem(Problem problem) {
        problem = problemRepository.save(problem);
        logger.info("Create problem: {}", problem.getId());
        return problem;
    }

    @Override
    public Problem getProblemById(int id) {
        return problemRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Problem> getProblemList() {
        return problemRepository.findAll();
    }

    @Override
    public Iterable<IOPair> getSamplesById(int id) {
        List<IOPair> ioPairs = problemRepository.findById(id).map(Problem::getIoPairs).orElse(null);
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.SAMPLE)).toList();
    }

    @Override
    public Iterable<IOPair> getTestsById(int id) {
        List<IOPair> ioPairs = problemRepository.findById(id).map(Problem::getIoPairs).orElse(null);
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.TEST)).toList();
    }

    @Override
    public Problem updateProblem(Problem problem) {
        problem = this.problemRepository.save(problem);
        logger.info("Update problem: {}", problem.getId());
        return problem;
    }

    @Override
    public void deleteProblem(Problem problem) {
        this.problemRepository.delete(problem);
        logger.info("Delete problem: {}", problem.getId());
    }

    @Override
    public List<Submission> getAllSubmissions(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getSubmissions)
                .orElse(List.of());
    }

    @Override
    public List<Submission> getPassedSubmissions(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getSubmissions)
                .map(submissions -> submissions
                        .stream()
                        .filter(submission -> submission
                                .getStatus()
                                .equals(Submission.Status.PASSED)
                        )
                        .toList()
                )
                .orElse(List.of());
    }

    @Override
    public int getSubmissionCount(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getSubmit)
                .orElse(0);
    }

    @Override
    public int getPassedSubmissionCount(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getPassed)
                .orElse(0);
    }
}
