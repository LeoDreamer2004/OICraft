package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.IOPair;

import java.util.List;

public interface IOPairDao {
    /**
     * Save a IOPair to the database
     *
     * @param ioPair the IOPair to create
     * @return A {@code IOPair} class with the IOPair's ID set.
     */
    IOPair saveIOPair(IOPair ioPair);

    /**
     * Delete a IOPair from the database
     *
     * @param ioPair the IOPair to delete
     */
    void deleteIOPair(IOPair ioPair);

    /**
     * Add a list of IOPairs to the database
     *
     * @param ioPairs the list of IOPairs to add
     */
    void addIOPairs(List<IOPair> ioPairs);

    /**
     * delete all IOPair with the problem id
     *
     * @param problemId the problem id which shows the IOPairs.
     */
    void deleteIOPairByProblemId(int problemId);
}
