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
    @Autowired
    private ProblemRepository problemRepository;

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
    public List<IOPair> getSamplesById(int id) {
        // TODO: Implement this method


        // A temporary implementation for testing
        return List.of(
                new IOPair(1, "1 2", "3", IOPair.Type.SAMPLE, 10),
                new IOPair(1, "3 4", "7", IOPair.Type.SAMPLE, 10)
        );
    }

    @Override
    public List<IOPair> getTestsById(int id) {
        return List.of();
    }
}
