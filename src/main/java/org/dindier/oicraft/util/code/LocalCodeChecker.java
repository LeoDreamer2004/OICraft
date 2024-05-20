package org.dindier.oicraft.util.code;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class LocalCodeChecker extends CodeChecker {
    private long startTime;
    private Process process;
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public CodeChecker setIO(String code, String language, String input, String output) throws IOException {
        // working directory
        this.workingDirectory = new File(FOLDER + System.currentTimeMillis() + "/");
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new IOException("Fail to create working dictionary");
        }

        // code
        String extension = extensionsMap.get(language);
        if (extension == null)
            log.error("Unsupported language: {}", language);
        this.codePath = workingDirectory.getPath() + "/Main." + extension;
        File codeFile = new File(codePath);
        codeFile.createNewFile();
        try (FileWriter fileWriter = new FileWriter(codeFile)) {
            fileWriter.write(code);
        }

        // language
        this.language = language;

        // input
        this.inputData = input + "\n"; // avoid EOF

        // output
        this.expectedOutput = output.stripTrailing();

        return this;
    }

    @Override
    public void test(boolean clearFile) throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilder();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerTask();
            }
        };
        Timer timer = new Timer();

        if (pb == null) {
            if (clearFile)
                clearFiles();
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
            } catch (IOException e) {
                // Exception often occurs when the code does not accept any input
                // It seems weird, but we still test the code as usual
            }
        }

        process.waitFor();
        usedTime = (int) (System.currentTimeMillis() - startTime);
        timer.cancel();

        if (status.equals("TLE") || status.equals("MLE")) {
            if (clearFile)
                clearFiles();
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
                log.error("Unsupported language: {}", language);
                return null;
            }
        }

        // if the compiler is not null, compile the code
        if (compiler != null) {
            String compileError = compiler.compile(new File(codePath), workingDirectory);
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
        output = new String(inputStream.readAllBytes()).stripTrailing();

        if (output.equals(expectedOutput)) {
            status = "AC";
            info = "Answer Accepted";
        } else if (process.exitValue() != 0) {
            if (info.isEmpty()) {
                // Not killed by timer, which means it's a runtime error
                status = "RE";
                info = "Runtime Error";
            }
        } else {
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

    private static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        if (!folder.delete()) {
            log.warn("Failed to delete folder {}, you may need to delete it by yourself",
                    folder);
        }
    }
}
