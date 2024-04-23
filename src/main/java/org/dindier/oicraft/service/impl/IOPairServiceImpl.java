package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.service.IOPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service("IOPairService")
public class IOPairServiceImpl implements IOPairService {
    private IOPairDao ioPairDao;

    // Use me as the dir to save the zip file
    private static final String tempZipDir = ".temp_zip";

    @Autowired
    public void setIOPairDao(IOPairDao ioPairDao) {
        this.ioPairDao = ioPairDao;
    }

    @Override
    public void addIOPairByZip(InputStream fileStream, int problemId) throws IOException {
        List<IOPair> ioPairs = new ArrayList<>();
        // TODO
        ioPairDao.deleteIOPairByProblemId(problemId);
        ioPairDao.addIOPairs(ioPairs);
        fileStream.close();
    }

    @Override
    public InputStream getIOPairsStream(int problemId) throws IOException {
        // TODO
        return null;
    }
}
