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
     * Get a IOPair by ID
     *
     * @param id the ID of the IOPair
     * @return A {@code IOPair} class if the IOPair exists, {@code null} otherwise.
     */
    @SuppressWarnings("unused")
    IOPair getIOPairById(int id);

    /**
     * Add a list of IOPairs to the database
     *
     * @param ioPairs the list of IOPairs to add
     * @return A list of {@code IOPair} classes with the IOPair's ID set.
     */
    @SuppressWarnings("UnusedReturnValue")
    Iterable<IOPair> addIOPairs(List<IOPair> ioPairs);

    /**
     * Get all IOPairs in the database
     *
     * @return A list of all {@code IOPair} classes in the database.
     * If there is no IOPair, an empty list will be returned.
     */
    List<IOPair> getIOPairByProblemId(int problemId);

    /**
     * delete all IOPair with the problem id
     *
     * @param problemId the problem id which shows the IOPairs.
     */
    void deleteIOPairByProblemId(int problemId);
}
