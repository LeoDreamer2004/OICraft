package org.dindier.oicraft.service;

import org.dindier.oicraft.model.IOPair;

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
     * @return 0 if success, -1 if the file is invalid
     */
    int addIOPairByZip(InputStream fileStream, int problemId) throws IOException;

    /**
     * Get IOPairs in zip file as a stream
     *
     * @param problemId the problem id
     * @return the stream of the zip file
     */
    InputStream getIOPairsStream(int problemId) throws IOException;
}
