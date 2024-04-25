package org.dindier.oicraft.service;

import java.io.IOException;
import java.io.InputStream;

public interface IOPairService {

    /**
     * add IOPair from zip file uploaded by user
     * @param fileStream the file stream uploaded
     * @return  0 if success, -1 if the file is invalid
     */
    int addIOPairByZip(InputStream fileStream, int problemId) throws IOException;

    InputStream getIOPairsStream(int problemId) throws IOException;
}
