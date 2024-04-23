package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;

import java.util.List;

public interface IOPairDao {

    IOPair createIOPair(IOPair ioPair);

    IOPair updateIOPair(IOPair ioPair);

    void deleteIOPair(IOPair ioPair);

    IOPair getIOPairById(int id);

    void addIOPairs(List<IOPair> ioPairs);

    List<IOPair> getIOPairByProblemId(int problemId);

    /**
     * delete all IOPair with the problem id
     *
     * @param problemId the problem id which shows the IOPairs.
     */
    void deleteIOPairByProblemId(int problemId);
}
