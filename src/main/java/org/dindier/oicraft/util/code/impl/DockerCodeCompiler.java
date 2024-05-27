package org.dindier.oicraft.util.code.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * A code compiler that runs the code in a docker container
 * <p>It will compile the code and return the error message if any
 *
 * @author Crl
 */
@Getter
@Slf4j
public enum DockerCodeCompiler {
    JAVA("code-checker-java"),
    CPP("code-checker-cpp"),
    C("code-checker-c");

    private static final String workingDirectory = "/usr/src/test";

    private final String dockerImage;

    DockerCodeCompiler(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String compile(File sourceFile, String dockerContainer, boolean createContainer) {
        ProcessBuilder pb;
        String sourceFilePath;
        if (createContainer) {
            pb = new ProcessBuilder("docker", "run", "--name", dockerContainer, "-d",
                    "-it", dockerImage, "/bin/bash");
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                log.error("Failed to create docker container {}", dockerContainer);
                return "Failed to run docker container";
            }
        }
        if (sourceFile != null) {
            sourceFilePath = sourceFile.getAbsolutePath();
            pb = new ProcessBuilder("docker", "cp", sourceFilePath, dockerContainer + ":" + workingDirectory);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                log.error("Failed to copy source file to docker image {}", dockerImage);
                return "Failed to copy source file to docker image";
            }
        }
        pb = new ProcessBuilder("docker", "container", "exec", dockerContainer, "./compile.sh");
        try {
            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0) {
                return new String(p.getErrorStream().readAllBytes());
            }
        } catch (Exception e) {
            log.error("Failed to compile source file {}", sourceFile);
            return "Failed to compile source file";
        }
        return null;
    }
}
