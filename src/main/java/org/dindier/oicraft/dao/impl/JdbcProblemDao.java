package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("problemDao")
public class JdbcProblemDao implements ProblemDao {
    private ProblemRepository problemRepository;

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    public void createProblem(Problem problem) {
        problemRepository.save(problem);
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
    public Iterable<IOPair> getSamplesByProblemId(int id) {
        List<IOPair> ioPairs = problemRepository.findById(id).map(Problem::getIoPairs).orElse(null);
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.SAMPLE)).toList();
    }

    @Override
    public Iterable<IOPair> getTestsByProblemId(int id) {
        List<IOPair> ioPairs = problemRepository.findById(id).map(Problem::getIoPairs).orElse(null);
        if (ioPairs == null) {
            return List.of();
        }
        return ioPairs.stream().filter(ioPair -> ioPair.getType().equals(IOPair.Type.TEST)).toList();
    }
}
