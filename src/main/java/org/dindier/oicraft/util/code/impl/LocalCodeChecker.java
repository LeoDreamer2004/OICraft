package org.dindier.oicraft.util.code.impl;

import org.apache.tomcat.jni.LibraryNotFoundError;
import org.dindier.oicraft.assets.constant.ConfigConstants;
import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.util.code.CodeChecker;
import org.dindier.oicraft.util.code.CodeCheckerInitializer;
import org.dindier.oicraft.util.code.lang.Language;
import org.dindier.oicraft.util.code.lang.Platform;
import org.dindier.oicraft.util.code.lang.Status;
import org.springframework.lang.Nullable;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * A code checker that runs the code in the local environment
 *
 * @author LeoDreamer
 */
public class LocalCodeChecker extends CodeChecker {
    private long startTime;
    private Process process;

    static {
        // load the native library
        URL url;
        if (CodeCheckerInitializer.platform == Platform.LINUX) {
            url = LocalCodeChecker.class.getClassLoader().getResource("lib/CodeChecker.so");
        } else if (CodeCheckerInitializer.platform == Platform.WINDOWS) {
            url = LocalCodeChecker.class.getClassLoader().getResource("lib/CodeChecker.dll");
        } else {
            throw new LibraryNotFoundError("CodeChecker", "Unsupported platform");
        }
        if (url != null) {
            System.load(url.getPath());
        }
    }

    @Override
    public CodeChecker setIO(String code, String language,
                             String input, @Nullable String output) throws CodeCheckerError {
        input = (input + "\n").replace("\r", "").replace("\n", System.lineSeparator());
        output = output == null ? null : output.replace("\r", "").replace("\n",
                System.lineSeparator());
        super.setIO(code, language, input, output);

        // working directory
        this.workingDirectory = new File(ConfigConstants.TEST_FOLDER + id + "/");
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new CodeCheckerError("Fail to create working dictionary");
        }

        // code
        String extension = extensionsMap.get(Language.fromString(language));
        if (extension == null)
            throw new CodeCheckerError("Unsupported language: " + language);
        codePath = workingDirectory.getPath() + "/Main." + extension;
        createAndWriteToFile(codePath, code);
        return this;
    }

    @Override
    public void test(boolean clearFile) throws CodeCheckerError {
        if (this.status == Status.CE) {
            clearFiles(clearFile);
            return;
        }
        this.status = Status.P;
        this.info = "";
        ProcessBuilder pb = getProcessBuilder();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerTask();
            }
        };
        Timer timer = new Timer();

        if (pb == null) {
            clearFiles(clearFile);
            return;
        }
        try {
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

            if (!(status == Status.TLE) && !(status == Status.MLE)) {
                checkAnswer();
            }
        } catch (Exception e) {
            throw new CodeCheckerError(e);
        }

        if (status == Status.P) status = Status.UKE;
        clearFiles(clearFile);
    }

    /* Get the ProcessBuilder for the code to be checked */
    private ProcessBuilder getProcessBuilder() throws CodeCheckerError {
        LocalCodeCompiler compiler = null;
        String[] runCmd;
        String path = workingDirectory.getPath();
        String exeName = CodeCheckerInitializer.platform == Platform.LINUX ? "main" : "main.exe";

        switch (language) {
            case PYTHON -> runCmd = new String[]{"python", codePath};
            case JAVA -> {
                compiler = LocalCodeCompiler.JAVA;
                runCmd = new String[]{"java", "-cp", path,
                        codePath.substring(codePath.lastIndexOf("/") + 1, codePath.length() - 5)};
            }
            case CPP -> {
                compiler = LocalCodeCompiler.CPP;
                runCmd = new String[]{path + "/" + exeName};
            }
            case C -> {
                compiler = LocalCodeCompiler.C;
                runCmd = new String[]{path + "/" + exeName};
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
                status = Status.CE;
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
            status = Status.TLE;
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
            status = Status.MLE;
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
            status = Status.AC;
            info = "Answer Accepted";
        } else if (process.exitValue() != 0) {
            if (info.isEmpty()) {
                // Not killed by timer, which means it's a runtime error
                status = Status.RE;
                info = "Runtime Error";
            }
        } else if (expectedOutput != null) {
            status = Status.WA;
            info = checkDifference(expectedOutput, output);
        }
    }

    private void clearFiles(boolean clearFile) {
        if (clearFile && workingDirectory.exists()) {
            if (CodeCheckerInitializer.platform == Platform.WINDOWS) {
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
