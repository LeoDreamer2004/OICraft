package org.dindier.oicraft.service.impl;

import net.lingala.zip4j.ZipFile;
import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.service.IOPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public void addIOPairByZip(ZipFile file) throws IOException {
        List<IOPair> ioPairs = new ArrayList<>();
        // TODO
        ioPairDao.addIOPairs(ioPairs);
    }
}
