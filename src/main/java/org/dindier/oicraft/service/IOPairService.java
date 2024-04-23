package org.dindier.oicraft.service;

import net.lingala.zip4j.ZipFile;

import java.io.IOException;

public interface IOPairService {

    /**
     * add IOPair from zip file uploaded by user
     * @param file the file uploaded
     */
    void addIOPairByZip(ZipFile file) throws IOException;
}
