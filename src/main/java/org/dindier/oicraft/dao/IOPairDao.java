package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;

import java.util.List;

public interface IOPairDao {

    void createIOPair(IOPair ioPair);

    void updateIOPair(IOPair ioPair);

    void deleteIOPair(IOPair ioPair);

    IOPair getIOPairById(int id);

    void addIOPairs(List<IOPair> ioPairs);

    List<IOPair> getIOPairByProblemId(int problemId);
}
