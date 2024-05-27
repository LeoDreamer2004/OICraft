package org.dindier.oicraft.util.code;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.util.code.lang.Language;
import org.dindier.oicraft.util.code.lang.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * An initializer for the code checker.
 * <p>Read the system platform and set the platform variable.
 * Initialize the code checker environment, detect whether docker is installed and running.
 * If docker is not installed or not running, use local environment instead
 *
 * @author Crl
 */
@Slf4j
public class CodeCheckerInitializer {
    public static boolean useDocker = false;
    public static final Platform platform = Platform.detect();
    public static final Map<Language, String> dockerImages = Map.of(
            Language.JAVA, "code-checker-java",
            Language.C, "code-checker-c",
            Language.CPP, "code-checker-cpp",
            Language.PYTHON, "code-checker-python"
    );

    /**
     * Initialize the code checker environment
     * Run when the service starts
     */
    public static void init() {
        // Read if we should use docker from the environment variable
        if (System.getenv("USE_DOCKER") != null && System.getenv("USE_DOCKER").equals("false")) {
            log.warn("USE_DOCKER is set to false, use local environment instead");
            return;
        }
        useDocker = detectDocker();
        if (!useDocker) {
            log.warn("Docker is not installed or not running, use local environment instead");
            return;
        }

        // build the docker image
        String dockerFilePath =
                Objects.requireNonNull(CodeCheckerInitializer.class.getClassLoader()
                        .getResource("scripts/docker")).getPath();
        if (platform == Platform.WINDOWS) {
            dockerFilePath = dockerFilePath.substring(1);
        }
        int dockerImagesSize = dockerImages.size();
        Thread[] threads = new Thread[dockerImagesSize];
        List<Language> dockerImagesKey = new ArrayList<>(dockerImages.keySet());
        for (int i = 0; i < dockerImagesSize; i++) {
            Language language = dockerImagesKey.get(i);
            String imageName = dockerImages.get(dockerImagesKey.get(i));
            String finalDockerFilePath = dockerFilePath;
            threads[i] = new Thread(() -> {
                log.info("Building docker image for {}", language.getDisplayName());
                if (!buildDockerImage(new File(finalDockerFilePath + "/" +
                        language.getDisplayName().toLowerCase()), imageName)) {
                    useDocker = false;
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("Failed to build docker image");
                useDocker = false;
            }
        }
        if (useDocker) {
            log.info("Docker is ready");
        }
    }

    /**
     * Build the docker image for the code checker
     *
     * @param dockerFilePath The path of the docker file
     * @param imageName      The name of the image
     * @return Whether the image is built successfully
     */
    private static boolean buildDockerImage(File dockerFilePath, String imageName) {
        try {
            Process checkProcess = Runtime.getRuntime().exec("docker images " + imageName);
            checkProcess.waitFor();
            InputStream checkInputStream = checkProcess.getInputStream();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(checkInputStream));
            if (bufferedReader.lines().count() > 1) {
                log.info("Docker image {} already exists", imageName);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to check docker image {}", imageName);
            return false;
        }

        Process process;
        try {
            process =
                    new ProcessBuilder("docker", "build", "-t", imageName, dockerFilePath.getPath()).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Failed to build docker image {}", imageName);
            return false;
        }
        if (process.exitValue() != 0) {
            log.error("Failed to build docker image {}", imageName);
            try {
                System.out.println(new String(process.getErrorStream().readAllBytes()));
            } catch (IOException e) {
                return false;
            }
            return false;
        }
        return true;
    }

    /* Detect whether docker is installed and running */
    private static boolean detectDocker() {
        Process process;
        try {
            process = new ProcessBuilder("docker", "info").start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return process.exitValue() == 0;
    }
}
