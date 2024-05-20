package org.dindier.oicraft.util.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
class DockerCodeChecker extends CodeChecker {
    private static final int TLE_EXIT_CODE = 124;

    private boolean containerCreated = false;

    @Override
    public DockerCodeChecker setIO(String code, String language, String input,
                                   @Nullable String output) throws IOException {
        input = (input + "\n").replace("\r", "");
        output = output == null ? "" : output.replace("\r", "");
        super.setIO(code, language, input, output);
        workingDirectory = new File(FOLDER + id + "/");
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            log.error("Fail to create working dictionary");
            return this;
        }
        codePath = workingDirectory.getAbsolutePath() + "/Main." + extensionsMap.get(language);
        String inputFilePath = workingDirectory.getPath() + "/input.txt";

        File file = createAndWriteToFile(codePath, code);
        File inputFile = createAndWriteToFile(inputFilePath, input);
        if (file == null || inputFile == null) {
            return this;
        }

        if (!containerCreated) {
            // create docker container
            try {
                if (language.equals("C++")) {
                    language = "Cpp";
                }
                String command = "docker run --name " + id + " -d -it "
                        + DockerInitializer.dockerImages.get(language) + " /bin/bash";
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                containerCreated = process.exitValue() == 0;
            } catch (IOException | InterruptedException e) {
                log.error("Error occurred while creating docker container", e);
            }
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
            return false;
        }
    }

    private static boolean removeDockerContainer(String container) {
        try {
            Process process = Runtime.getRuntime().exec("docker rm " + container + " -f");
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private boolean compile() {
        if (compiled) {
            return true;
        }
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
        compiled = true;
        return true;
    }

    public void test(boolean clearFile) throws IOException, InterruptedException {
        // if the code is already compiled, skip the compile step
        if (this.status.equals("CE")) {
            clear(clearFile);
            return;
        }
        this.status = "P";
        this.info = "Pending";
        if (!compile()) {
            clear(clearFile);
            return;
        }

        // run the code
        String command =
                "docker exec " + id + " ./run.sh " + timeLimit / 1000.0 + " " + memoryLimit * 100;
        Process runProcess = Runtime.getRuntime().exec(command);
        runProcess.waitFor();

        // get the output and error stream
        String stdout = new String(runProcess.getInputStream().readAllBytes());
        String stderr = new String(runProcess.getErrorStream().readAllBytes());
        setUsedTimeAndMemory(stdout, stderr);
        if (this.usedMemory > this.memoryLimit) {
            this.status = "MLE";
            this.info = "Memory Limit Exceeded";
            clear(clearFile);
            return;
        }
        if (status.equals("TLE") || status.equals("MLE") || status.equals("RE")) {
            clear(clearFile);
            return;
        }

        // check the output
        if (!copyFromDockerToHost(String.valueOf(id),
                new File(workingDirectory.getAbsoluteFile() + "/output.txt"))) {
            this.status = "RE";
            this.info = "Runtime Error";
            clear(clearFile);
            return;
        }
        try (FileReader fileReader = new FileReader(workingDirectory.getAbsoluteFile() + "/output.txt")) {
            StringBuilder output = new StringBuilder();
            int c;
            while ((c = fileReader.read()) != -1) {
                output.append((char) c);
            }
            this.output = output.toString().stripTrailing().replace("\r", "");
        }
        checkAnswer();
        clear(clearFile);
    }

    private void checkAnswer() {
        if (this.expectedOutput == null) {
            this.status = "AC";
            this.info = "Accepted";
            return;
        }

        // deal with different line ending
        if (output.equals(expectedOutput)) {
            this.status = "AC";
            this.info = "Accepted";
        } else {
            this.status = "WA";
            this.info = checkDifference(expectedOutput, output);
        }

    }

    private void setUsedTimeAndMemory(String stdout, String stderr) {
        String[] lines = (stdout + stderr).split("\n");
        String memoryHeader = "Memory Usage: ";
        String timeHeader = "Used time: ";
        String exitCodeHeader = "Exit code: ";
        for (String line : lines) {
            if (line.startsWith(memoryHeader)) {
                this.usedMemory = Integer.parseInt("0" + line.substring(memoryHeader.length()));
            } else if (line.startsWith(timeHeader)) {
                this.usedTime = (int) (Double.parseDouble("0" + line.substring(timeHeader.length())) * 1000);
            } else if (line.startsWith(exitCodeHeader)) {
                int exitCode = Integer.parseInt("0" + line.substring(exitCodeHeader.length()));
                if (exitCode == TLE_EXIT_CODE) {
                    this.status = "TLE";
                    this.info = "Time Limit Exceeded";
                } else if (exitCode != 0) {
                    this.status = "RE";
                    this.info = "Runtime Error";
                }
            }
        }
    }

    private void clear(boolean clear) {
        if (clear) {
            if (!removeDockerContainer(String.valueOf(id))) {
                log.error("Error occurred while removing docker container");
            }
            deleteFolder(workingDirectory);
        }
    }
}
