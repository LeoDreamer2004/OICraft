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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Comparator;
import java.util.zip.ZipOutputStream;

import org.mozilla.universalchardet.UniversalDetector;

@Service("IOPairService")
public class IOPairServiceImpl implements IOPairService {
    private IOPairDao ioPairDao;
    private ProblemDao problemDao;

    // Use me as the dir to save the zip file
    private static final String putZipDir = ".temp_zip_put";
    private static int id = 0;
    private static final String getZipDir = ".temp_zip_get";
    private static int id2 = 0;
    private static final String tempZipDir = ".temp_zip";

    @Autowired
    public void setIOPairDao(IOPairDao ioPairDao) {
        this.ioPairDao = ioPairDao;
    }

    @Override
    public void addIOPairByZip(InputStream fileStream, int problemId) throws IOException {
        Problem problem = problemDao.getProblemById(problemId);
        List<IOPair> ioPairs = new ArrayList<>();
        Path tempDir = Files.createTempDirectory(putZipDir + id++);
        try (ZipInputStream zipInputStream = new ZipInputStream(fileStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.getName().endsWith(".in")) {
                    Path filePath = tempDir.resolve(entry.getName());
                    Files.copy(zipInputStream, filePath);
                    String input = new String(Files.readAllBytes(filePath), detectCharset(filePath));
                    String outputFile = entry.getName().replace(".in", ".out");
                    // may throw exception if the output file is not found
                    Path outputFilePath = tempDir.resolve(outputFile);
                    String output = new String(Files.readAllBytes(outputFilePath), detectCharset(outputFilePath));
                    String scoreFile = entry.getName().replace(".in", ".score");
                    Path scoreFilePath = tempDir.resolve(scoreFile);
                    String score = new String(Files.readAllBytes(scoreFilePath), detectCharset(scoreFilePath));
                    int scoreInt = Integer.parseInt(score);
                    IOPair.Type type = entry.getName().toLowerCase().startsWith("sample/") ?
                            IOPair.Type.SAMPLE :
                            IOPair.Type.TEST;
                    IOPair ioPair = new IOPair(input, output, type, scoreInt);
                    ioPair.setProblem(problem);
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
        List<IOPair> ioPairs = ioPairDao.getIOPairByProblemId(problemId);
        Path tempDir = Files.createTempDirectory(getZipDir + id2++);
        for (IOPair ioPair : ioPairs) {
            String dir = ioPair.getType() == IOPair.Type.SAMPLE ? "sample" : "test";
            Path dirPath = tempDir.resolve(dir);
            Files.createDirectories(dirPath);
            Path inputFilePath = dirPath.resolve(ioPair.getId() + ".in");
            Files.writeString(inputFilePath, ioPair.getInput());
            Path outputFilePath = dirPath.resolve(ioPair.getId() + ".out");
            Files.writeString(outputFilePath, ioPair.getOutput());
        }
        Path zipDir = Paths.get(tempZipDir + id2);
        Files.createDirectories(zipDir);
        Path tempZipPath = Files.createTempFile(zipDir, "ioPairs", ".zip");
        File tempZipFile = tempZipPath.toFile();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZipFile))) {
            try (Stream<Path> paths = Files.walk(tempDir)) {
                paths.filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(tempDir.relativize(path).toString());
                            try {
                                zos.putNextEntry(zipEntry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
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

            return new FileInputStream(tempZipFile) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        Files.deleteIfExists(tempZipPath);
                        Files.deleteIfExists(zipDir);
                    }
                }
            };
        }
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
