package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.dao.repository.IOPairRepository;
import org.dindier.oicraft.dao.repository.ProblemRepository;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("IOPairDao")
public class JpaIOPairDao implements IOPairDao {
    private IOPairRepository ioPairRepository;
    private ProblemRepository problemRepository;
    private final Logger logger = LoggerFactory.getLogger(JpaIOPairDao.class);

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
        ioPair = ioPairRepository.save(ioPair);
        logger.info("Create IOPair: {}", ioPair.getId());
        return ioPair;
    }

    @Override
    public IOPair updateIOPair(IOPair ioPair) {
        ioPair = ioPairRepository.save(ioPair);
        logger.info("Update IOPair: {}", ioPair.getId());
        return ioPair;
    }

    @Override
    public void deleteIOPair(IOPair ioPair) {
        ioPairRepository.delete(ioPair);
        logger.info("Delete IOPair: {}", ioPair.getId());
    }

    @Override
    public IOPair getIOPairById(int id) {
        return ioPairRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<IOPair> addIOPairs(List<IOPair> ioPairs) {
        logger.info("Add {} IOPairs", ioPairs.size());
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
        logger.info("Delete IOPairs by problem id: {}", problemId);
    }
}
