package org.dindier.oicraft.service;

import org.dindier.oicraft.assets.exception.BadFileException;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;

import java.io.IOException;
import java.io.InputStream;

public interface IOPairService {

    /**
     * Save a IOPair to the database
     *
     * @param ioPair the IOPair to create
     * @return A {@code IOPair} class with the IOPair's ID set.
     */
    @SuppressWarnings("UnusedReturnValue")
    IOPair saveIOPair(IOPair ioPair);

    /**
     * Add IOPair from zip file uploaded by user
     *
     * @param fileStream The file stream uploaded
     * @throws BadFileException If the file is not a valid zip file
     */
    void addIOPairByZip(InputStream fileStream, int problemId) throws BadFileException;

    /**
     * Get IOPairs in zip file as a stream
     *
     * @param problem the problem
     * @return the stream of the zip file
     */
    InputStream getIOPairsStream(Problem problem) throws IOException;
}
