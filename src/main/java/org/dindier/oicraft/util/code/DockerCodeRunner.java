package org.dindier.oicraft.util.code;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class DockerCodeRunner extends CodeChecker {
    private static final int TLE_EXIT_CODE = 124;
    private static final int MLE_EXIT_CODE = 137;

    private long id;

    /**
     * Set the code, language, input and expected output
     *
     * @implNote This method will create a new docker container.
     */
    public DockerCodeRunner setIO(String code, String language, String input, String expectedOutput) {
        this.language = language;
        this.inputData = input;
        this.expectedOutput = expectedOutput;
        this.id = System.currentTimeMillis();
        this.codePath = FOLDER + id + "/Main." + extensionsMap.get(language);
        String inputPath = FOLDER + id + "/input.txt";

        File file = createAndWriteToFile(codePath, code);
        if (file == null) {
            log.error("Error occurred while creating file");
            return this;
        }
        File inputFile = createAndWriteToFile(inputPath, input);
        if (inputFile == null) {
            log.error("Error occurred while creating file");
            return this;
        }

        // create docker container
        try {
            Process process = Runtime.getRuntime().exec("docker run -d --name " + id + " " + DockerInitializer.dockerImages.get(language));
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while creating docker container", e);
        }

        String container = String.valueOf(id);
        if (!(copyFromHostToDocker(container, file) && copyFromHostToDocker(container, inputFile))) {
            log.error("Error occurred while copying file to docker");
            return this;
        }
        return this;
    }

    private static boolean copyFromHostToDocker(String container, File file) {
        try {
            Process process = Runtime.getRuntime().exec("docker cp " + file.getAbsolutePath() +
                    " " + container + ":/usr/src/test/");
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while copying file to docker", e);
            return false;
        }
    }

    private static boolean copyFromDockerToHost(String container, File file) {
        try {
            Process process = Runtime.getRuntime().exec("docker cp " + container + ":/usr/src/test/"
                    + file.getName() + " " + file.getAbsolutePath());
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while copying file from docker", e);
            return false;
        }
    }

    private static boolean removeDockerContainer(String container) {
        try {
            Process process = Runtime.getRuntime().exec("docker rm " + container);
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while removing docker container", e);
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createAndWriteToFile(String path, String content) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
            return file;
        } catch (IOException e) {
            log.error("Error occurred while writing to file", e);
            return null;
        }
    }

    public DockerCodeRunner setLimit(int timeLimit, int memoryLimit) {
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        return this;
    }

    private boolean compile() {
        DockerCodeCompiler compiler = switch (language) {
            case "Java" -> DockerCodeCompiler.JAVA;
            case "C" -> DockerCodeCompiler.C;
            case "C++" -> DockerCodeCompiler.CPP;
            default -> null;
        };
        if (compiler != null) {
            String info = compiler.compile(null, String.valueOf(id), false);
            if (info != null) {
                this.info = info;
                this.status = "CE";
                return false;
            }
        }
        return true;
    }

    public void test(boolean clearFile) throws IOException, InterruptedException {
        if (!compile()) {
            return;
        }
        Process runProcess =
                Runtime.getRuntime().exec("docker exec " + id + " ./run.sh " + timeLimit + "ms " + memoryLimit);
        runProcess.waitFor();
        int exitCode = runProcess.exitValue();
        if (exitCode == TLE_EXIT_CODE) {
            this.status = "TLE";
            return;
        }
        if (exitCode == MLE_EXIT_CODE) {
            this.status = "MLE";
            return;
        }
        if (exitCode != 0) {
            this.status = "RE";
            return;
        }
        if (!copyFromDockerToHost(String.valueOf(id), new File(FOLDER + id + "/output.txt"))) {
            return;
        }
        try (FileReader fileReader = new FileReader(FOLDER + id + "/output.txt")) {
            StringBuilder output = new StringBuilder();
            int c;
            while ((c = fileReader.read()) != -1) {
                output.append((char) c);
            }
            this.output = output.toString();
        }
    }
}
