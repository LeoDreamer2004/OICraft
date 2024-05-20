package org.dindier.oicraft.util.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.*;

@Slf4j
class LocalCodeChecker extends CodeChecker {
    private long startTime;
    private Process process;

    @Override
    public CodeChecker setIO(String code, String language,
                             String input, @Nullable String output) throws IOException {
        input = (input + "\n").replace("\r", "").replace("\n", System.lineSeparator());
        output = output == null ? "" : output.replace("\r", "").replace("\n", System.lineSeparator());
        super.setIO(code, language, input, output);

        // working directory
        this.workingDirectory = new File(FOLDER + id + "/");
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new IOException("Fail to create working dictionary");
        }

        // code
        String extension = extensionsMap.get(language);
        if (extension == null)
            log.error("Unsupported language: {}", language);
        codePath = workingDirectory.getPath() + "/Main." + extension;
        createAndWriteToFile(codePath, code);
        return this;
    }

    @Override
    public void test(boolean clearFile) throws IOException, InterruptedException {
        if (this.status.equals("CE")) {
            if (clearFile) {
                clearFiles();
            }
            return;
        }
        this.status = "P";
        this.info = "Pending";
        ProcessBuilder pb = getProcessBuilder();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerTask();
            }
        };
        Timer timer = new Timer();

        if (pb == null) {
            if (clearFile) {
                clearFiles();
            }
            return;
        }

        process = pb.start();
        // read the memory at first in case of the process terminated too quickly
        usedMemory = (int) (getProcessMemoryUsage(process.pid()) / 1024);
        startTime = System.currentTimeMillis();
        timer.schedule(timerTask, 0, 20);

        if (!inputData.isEmpty()) {
            // If the code needs input, write the input to the process
            OutputStream outputStream = process.getOutputStream();
            BufferedWriter outputStreamWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            try {
                outputStreamWriter.write(inputData);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException ignored) {
                // Exception often occurs when the code does not accept any input
                // It seems weird, but we still test the code as usual
            }
        }

        process.waitFor();
        usedTime = (int) (System.currentTimeMillis() - startTime);
        timer.cancel();

        if (status.equals("TLE") || status.equals("MLE")) {
            if (clearFile) {
                clearFiles();
            }
            return;
        }

        checkAnswer();
        if (clearFile) {
            clearFiles();
        }
    }

    /* Get the ProcessBuilder for the code to be checked */
    private ProcessBuilder getProcessBuilder() {
        LocalCodeCompiler compiler = null;
        String[] runCmd;

        switch (language) {
            case "Python" -> {
                runCmd = new String[2];
                runCmd[0] = "python";
                runCmd[1] = codePath;
            }
            case "Java" -> {
                compiler = LocalCodeCompiler.JAVA;
                runCmd = new String[4];
                runCmd[0] = "java";
                runCmd[1] = "-cp";
                runCmd[2] = workingDirectory.getPath();
                runCmd[3] = codePath.substring(codePath.lastIndexOf("/") + 1, codePath.length() - 5);
            }
            case "C++" -> {
                compiler = LocalCodeCompiler.CPP;
                runCmd = new String[1];
                runCmd[0] = workingDirectory.getPath() + (platform.equals("Linux") ? "/main" :
                        "/main.exe");
            }
            case "C" -> {
                compiler = LocalCodeCompiler.C;
                runCmd = new String[1];
                runCmd[0] = workingDirectory.getPath() + (platform.equals("Linux") ? "/main" :
                        "/main.exe");
            }
            default -> {
                return null;
            }
        }

        // if the compiler is not null, compile the code
        if (!compiled && compiler != null) {
            String compileError = compiler.compile(new File(codePath), workingDirectory);
            compiled = true;
            if (compileError != null) {
                status = "CE";
                info = compileError;
                return null;
            }
        }
        return new ProcessBuilder(runCmd);
    }

    /* A timer task, which will terminate the testing process when the program exceeded the limit */
    private void timerTask() {
        usedTime = (int) (System.currentTimeMillis() - startTime);
        if (usedTime > timeLimit) {
            status = "TLE";
            info = "Time Limit Exceeded";
            terminateProcess();
        }

        long temp;
        try {
            temp = (int) (getProcessMemoryUsage(process.pid()) / 1024);
        } catch (Exception e) {
            return; // the process may have been terminated
        }

        if (temp > memoryLimit) {
            status = "MLE";
            info = "Memory Limit Exceeded";
            terminateProcess();
        }
        if (temp > usedMemory) {
            usedMemory = (int) temp;
        }
    }

    private void terminateProcess() {
        // kill the child processes recursively
        process.descendants().forEach(ProcessHandle::destroy);
        process.destroy();
    }

    /* Check the answer */
    private void checkAnswer() throws IOException {
        InputStream inputStream = process.getInputStream();
        output = new String(inputStream.readAllBytes()).stripTrailing()
                .replace("\r", "").replace("\n", System.lineSeparator());

        if (output.equals(expectedOutput)) {
            status = "AC";
            info = "Answer Accepted";
        } else if (process.exitValue() != 0) {
            if (info.isEmpty()) {
                // Not killed by timer, which means it's a runtime error
                status = "RE";
                info = "Runtime Error";
            }
        } else if (expectedOutput != null) {
            status = "WA";
            info = checkDifference(expectedOutput, output);
        }
    }

    private void clearFiles() {
        if (workingDirectory.exists()) {
            if (platform.equals("Windows")) {
                // delete the folder after 5 seconds because on Windows,
                // if a process encounters runtime error, it may not exit immediately
                // It's not a good idea, but I don't have a better solution
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        deleteFolder(workingDirectory);
                    }
                }, 5000);
            } else {
                deleteFolder(workingDirectory);
            }
        }
    }
}
