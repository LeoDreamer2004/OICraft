package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.service.IOPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Comparator;

import org.mozilla.universalchardet.UniversalDetector;

@Service("IOPairService")
public class IOPairServiceImpl implements IOPairService {
    private IOPairDao ioPairDao;
    private ProblemDao problemDao;

    // Use me as the dir to save the zip file
    private static final String tempZipDir = ".temp_zip";
    private static int id=0;

    @Autowired
    public void setIOPairDao(IOPairDao ioPairDao) {
        this.ioPairDao = ioPairDao;
    }

    @Override
    public void addIOPairByZip(InputStream fileStream, int problemId) throws IOException {
        // TODO
        Problem problem = problemDao.getProblemById(problemId);
        List<IOPair> ioPairs = new ArrayList<>();
        Path tempDir = Files.createTempDirectory(tempZipDir+File.separator+id++);
        try(ZipInputStream zipInputStream = new ZipInputStream(fileStream)){
            ZipEntry entry;
            while((entry = zipInputStream.getNextEntry()) != null){
                if(entry.isDirectory()){
                    continue;
                }
                if(entry.getName().endsWith(".in")){
                    Path filePath = tempDir.resolve(entry.getName());
                    Files.copy(zipInputStream, filePath);
                    String input = new String(Files.readAllBytes(filePath), detectCharset(filePath));
                    String outputFile = entry.getName().replace(".in", ".out");
                    //may throw exception if the output file is not found
                    Path outputFilePath = tempDir.resolve(outputFile);
                    String output = new String(Files.readAllBytes(outputFilePath), detectCharset(outputFilePath));
                    String scoreFile = entry.getName().replace(".in", ".score");
                    Path scoreFilePath = tempDir.resolve(scoreFile);
                    String score = new String(Files.readAllBytes(scoreFilePath), detectCharset(scoreFilePath));
                    int scoreInt = Integer.parseInt(score);
                    IOPair.Type type = entry.getName().startsWith("sample/") ? IOPair.Type.SAMPLE :
                            IOPair.Type.TEST;
                    IOPair ioPair = new IOPair(input, output, type, scoreInt);
                    ioPairs.add(ioPair);
                }
            }
        }
        ioPairDao.deleteIOPairByProblemId(problemId);
        ioPairDao.addIOPairs(ioPairs);
        fileStream.close();
        try (Stream<Path> paths = Files.walk(tempDir)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            throw new UncheckedIOException("Failed to delete file: " + file,
                                    new IOException());
                        }
                    });
        }
    }

    @Override
    public InputStream getIOPairsStream(int problemId) throws IOException {
        // TODO
        return null;
    }

    private String detectCharset(Path filePath) throws IOException {
        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(filePath.toFile());
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        fis.close();
        return encoding != null ? encoding : StandardCharsets.UTF_8.name();
    }

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }
}
