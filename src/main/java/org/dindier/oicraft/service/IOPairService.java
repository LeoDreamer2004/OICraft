package org.dindier.oicraft.service;

import java.io.IOException;
import java.io.InputStream;

public interface IOPairService {

    /**
     * add IOPair from zip file uploaded by user
     * @param fileStream the file stream uploaded
     */
    void addIOPairByZip(InputStream fileStream, int problemId) throws IOException;

    InputStream getIOPairsStream(int problemId) throws IOException;
}
