package org.dindier.oicraft.util.code.impl;

import org.dindier.oicraft.assets.exception.CodeCheckerError;

import java.io.File;
import java.io.IOException;

/**
 * A code compiler that runs the code in a docker container
 * <p>It will compile the code and return the error message if any
 *
 * @author Crl
 */
public enum DockerCodeCompiler {
    JAVA("code-checker-java"),
    CPP("code-checker-cpp"),
    C("code-checker-c");

    private static final String workingDirectory = "/usr/src/test";

    private final String dockerImage;

    DockerCodeCompiler(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String compile(File sourceFile, String dockerContainer, boolean createContainer)
            throws CodeCheckerError {
        ProcessBuilder pb;
        String sourceFilePath;
        if (createContainer) {
            pb = new ProcessBuilder("docker", "run", "--name", dockerContainer, "-d",
                    "-it", dockerImage, "/bin/bash");
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                throw new CodeCheckerError("Failed to create docker container " + dockerContainer);
            }
        }
        if (sourceFile != null) {
            sourceFilePath = sourceFile.getAbsolutePath();
            pb = new ProcessBuilder("docker", "cp", sourceFilePath, dockerContainer + ":" + workingDirectory);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                throw new CodeCheckerError("Failed to copy source file to docker image " + dockerImage);
            }
        }
        pb = new ProcessBuilder("docker", "container", "exec", dockerContainer, "./compile.sh");
        try {
            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0) {
                return new String(p.getErrorStream().readAllBytes());
            }
        } catch (IOException | InterruptedException e) {
            throw new CodeCheckerError("Failed to compile source file " + sourceFile);
        }
        return null;
    }
}
