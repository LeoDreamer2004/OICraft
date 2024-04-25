package org.dindier.oicraft.service;

import java.io.IOException;
import java.io.InputStream;

public interface IOPairService {

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
