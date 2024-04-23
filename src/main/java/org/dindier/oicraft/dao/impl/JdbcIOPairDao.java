package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.dao.repository.IOPairRepository;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("IOPairDao")
public class JdbcIOPairDao implements IOPairDao {
    private IOPairRepository ioPairRepository;
    private ProblemRepository problemRepository;

    @Autowired
    public void setIOPairRepository(IOPairRepository ioPairRepository) {
        this.ioPairRepository = ioPairRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    public IOPair createIOPair(IOPair ioPair) {
        return ioPairRepository.save(ioPair);
    }

    @Override
    public IOPair updateIOPair(IOPair ioPair) {
        return ioPairRepository.save(ioPair);
    }

    @Override
    public void deleteIOPair(IOPair ioPair) {
        ioPairRepository.delete(ioPair);
    }

    @Override
    public IOPair getIOPairById(int id) {
        return ioPairRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<IOPair> addIOPairs(List<IOPair> ioPairs) {
        return ioPairRepository.saveAll(ioPairs);
    }

    @Override
    public List<IOPair> getIOPairByProblemId(int problemId) {
        return problemRepository
                .findById(problemId)
                .map(Problem::getIoPairs)
                .orElse(List.of());
    }

    @Override
    public void deleteIOPairByProblemId(int problemId) {
        List<IOPair> ioPairs = getIOPairByProblemId(problemId);
        for (IOPair ioPair : ioPairs) {
            deleteIOPair(ioPair);
        }
    }
}
