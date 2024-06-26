package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.exception.BadFileException;
import org.dindier.oicraft.dao.IOPairDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.service.IOPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service("IOPairService")
@Slf4j
public class IOPairServiceImpl implements IOPairService {
    // Use me as the dir to save the zip file
    private static final String putZipDir = "temp" + File.separator + ".put";
    private static final String getZipDir = "temp" + File.separator + ".get";
    private static final String tempZipDir = "temp" + File.separator + ".temp";
    private static int setId = 0;
    private static int getId = 0;
    private IOPairDao ioPairDao;
    private ProblemDao problemDao;

    @Autowired
    public void setIOPairDao(IOPairDao ioPairDao) {
        this.ioPairDao = ioPairDao;
    }

    @Override
    public IOPair saveIOPair(IOPair ioPair) {
        if (ioPair.getId() == 0)
            log.info("Created IOPair for problem: {}", ioPair.getProblem().getId());
        else log.info("Save IOPair: {}", ioPair.getId());
        return ioPairDao.saveIOPair(ioPair);
    }

    @Override
    public void addIOPairByZip(InputStream fileStream, int problemId) throws BadFileException {
        Problem problem = problemDao.getProblemById(problemId);
        List<IOPair> ioPairs = new ArrayList<>();
        Path tempDir = Paths.get(putZipDir + File.separator + setId++);
        try (ZipInputStream zipInputStream = new ZipInputStream(fileStream)) {
            // extract the zip file
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                Path filePath = tempDir.resolve(entry.getName());
                try {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipInputStream, filePath);
                } catch (IOException e) {
                    throw new BadFileException("无法创建或拷贝临时文件路径");
                }
            }
        } catch (Exception e) {
            throw new BadFileException("无法读取文件，请检查是否为Zip或Zip内文件格式");
        }
        try (Stream<Path> paths = Files.walk(tempDir)) {
            // read the input and output files
            paths.filter(Files::isRegularFile).forEach(path -> {
                String fileName = path.getFileName().toString();
                if (fileName.endsWith(".in")) {
                    try {
                        String input = Files.readString(path).stripTrailing();
                        String outputFileName = fileName.replace(".in", ".out");
                        Path outputFilePath = path.resolveSibling(outputFileName);
                        String output = Files.readString(outputFilePath).stripTrailing();
                        String scoreFileName = fileName.replace(".in", ".score");
                        Path scoreFilePath = path.resolveSibling(scoreFileName);
                        String score = Files.readString(scoreFilePath).trim();
                        int scoreInt = Integer.parseInt(score);
                        IOPair.Type type = path.getParent().getFileName().toString().equals("sample") ?
                                IOPair.Type.SAMPLE :
                                IOPair.Type.TEST;
                        IOPair ioPair = new IOPair(problem, input, output, type, scoreInt);
                        ioPairs.add(ioPair);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new BadFileException("Zip内文件路径读取异常");
        }

        // delete the old IOPairs and add the new ones
        ioPairDao.deleteIOPairByProblemId(problemId);
        ioPairDao.addIOPairs(ioPairs);
        try {
            fileStream.close();
        } catch (IOException e) {
            log.warn("Cannot close the file stream. Please take care of the memory leak.");
        }
        try (Stream<Path> paths = Files.walk(tempDir)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            log.warn("Failed to delete file: {}", file);
                        }
                    });
        } catch (IOException e) {
            throw new BadFileException("Zip内文件路径读取异常");
        }
    }

    @Override
    public InputStream getIOPairsStream(Problem problem) throws IOException {
        List<IOPair> ioPairs = problem.getIoPairs();
        String folderPath = getZipDir + File.separator + getId++;
        Path tempDir = Files.createDirectories(Paths.get(folderPath));

        // write the input and output files
        for (IOPair ioPair : ioPairs) {
            String dir = ioPair.getType() == IOPair.Type.SAMPLE ? "sample" : "test";
            Path dirPath = tempDir.resolve(dir);
            Files.createDirectories(dirPath);
            Path inputFilePath = dirPath.resolve(ioPair.getId() + ".in");
            Files.writeString(inputFilePath, ioPair.getInput());
            Path outputFilePath = dirPath.resolve(ioPair.getId() + ".out");
            Files.writeString(outputFilePath, ioPair.getOutput());
            Path scoreFilePath = dirPath.resolve(ioPair.getId() + ".score");
            Files.writeString(scoreFilePath, String.valueOf(ioPair.getScore()));
        }

        // zip the files
        Path zipDir = Paths.get(tempZipDir + File.separator + getId);
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

            // return the zip file as a stream
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

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }
}
