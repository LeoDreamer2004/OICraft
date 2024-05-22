package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.repository.CheckpointRepository;
import org.dindier.oicraft.dao.repository.IOPairRepository;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("problemDao")
public class JpaProblemDao implements ProblemDao {
    private ProblemRepository problemRepository;
    private IOPairRepository ioPairRepository;
    private SubmissionRepository submissionRepository;
    private CheckpointRepository checkpointRepository;

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setIOPairRepository(IOPairRepository ioPairRepository) {
        this.ioPairRepository = ioPairRepository;
    }

    @Override
    public Problem saveProblem(Problem problem) {
        problem = problemRepository.save(problem);
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
    public void deleteProblem(Problem problem) {
        // Delete related ioPairs, submissions and checkpoints
        List<Submission> submissions = problem.getSubmissions();
        List<Checkpoint> checkpoints = submissions
                .stream()
                .map(Submission::getCheckpoints)
                .flatMap(List::stream)
                .toList();
        checkpointRepository.deleteAll(checkpoints);
        submissionRepository.deleteAll(submissions);
        ioPairRepository.deleteAll(problem.getIoPairs());
        problemRepository.delete(problem);
    }

    @Override
    public int getProblemCount() {
        return (int) problemRepository.count();
    }

    @Override
    public List<Problem> getProblemInRange(int start, int count) {
        return problemRepository.findProblemInRange(start, count);
    }

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setCheckpointRepository(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }
}
