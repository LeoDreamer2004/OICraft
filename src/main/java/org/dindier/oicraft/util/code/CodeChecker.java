package org.dindier.oicraft.util.code;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.LibraryNotFoundError;

import java.io.*;
import java.net.URL;
import java.util.*;

@Slf4j
public class CodeChecker {

    public static final String FOLDER = "temp/";
    private String codePath;
    private String language;
    private String inputData;
    private String expectedOutput;
    private int timeLimit = 0;
    private int memoryLimit = 0;
    // The working directory for the submission
    private File workingDirectory;

    private static final String platform;

    private static final Map<String, String> extensionsMap = Map.of(
            "Java", "java",
            "C", "c",
            "C++", "cpp",
            "Python", "py"
    );
    @Getter
    private String output;
    @Getter
    private int usedTime = 0;
    @Getter
    private int usedMemory = 0;
    @Getter
    private String status = "P";
    @Getter
    private String info = "";

    private long startTime;
    private Process process;

    static {
        // Load the native library
        URL url;
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            url = CodeChecker.class.getClassLoader().getResource("lib/CodeChecker.so");
            platform = "Linux";
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            url = CodeChecker.class.getClassLoader().getResource("lib/CodeChecker.dll");
            platform = "Windows";
        } else {
            throw new LibraryNotFoundError("CodeChecker", "Unsupported platform");
        }
        if (url != null) {
            System.load(url.getPath());
        }

        // create the folder if not exists
        File folder = new File(FOLDER);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new RuntimeException("Failed to create folder: " + FOLDER);
        }
    }

    /**
     * Set the IO files for the code to be checked
     *
     * @param code         The code to be checked
     * @param language     The language of the code
     * @param input        The input string
     * @param output       The expected output
     * @param submissionId The id of the submission
     * @return The CodeChecker itself
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CodeChecker setIO(String code, String language, String input, String output, int submissionId) throws IOException {
        // working directory
        this.workingDirectory = new File(FOLDER + submissionId + "/");
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new IOException("Fail to create working dictionary");
        }

        // code
        String extension = extensionsMap.get(language);
        if (extension == null)
            throw new IllegalArgumentException("Unsupported language: " + language);
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

    /**
     * Set the time and memory limit for the code to be checked
     *
     * @param timeLimit   The time limit in milliseconds
     * @param memoryLimit The memory limit in KB
     * @return The CodeChecker itself
     */
    public CodeChecker setLimit(int timeLimit, int memoryLimit) {
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        return this;
    }

    /**
     * Run the code and check the result
     * Use getter to get the status, info etc
     */
    public void test() throws IOException, InterruptedException {
        test(true);
    }

    /**
     * Run the code and check the result
     * Use getter to get the status, info etc
     *
     * @param clearFile Whether to clear the files after the test
     */
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
        CodeCompiler compiler = null;
        String[] runCmd;

        switch (language) {
            case "Python" -> {
                runCmd = new String[2];
                runCmd[0] = "python";
                runCmd[1] = codePath;
            }
            case "Java" -> {
                compiler = CodeCompiler.JAVA;
                runCmd = new String[4];
                runCmd[0] = "java";
                runCmd[1] = "-cp";
                runCmd[2] = workingDirectory.getPath();
                runCmd[3] = codePath.substring(codePath.lastIndexOf("/") + 1, codePath.length() - 5);
            }
            case "C++" -> {
                compiler = CodeCompiler.CPP;
                runCmd = new String[1];
                runCmd[0] = workingDirectory.getPath() + (platform.equals("Linux") ? "/main" :
                        "/main.exe");
            }
            case "C" -> {
                compiler = CodeCompiler.C;
                runCmd = new String[1];
                runCmd[0] = workingDirectory.getPath() + (platform.equals("Linux") ? "/main" :
                        "/main.exe");
            }
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
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

    private static native long getProcessMemoryUsage(long pid);

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

    /* Return the difference between expected and actual output */
    private static String checkDifference(String expected, String actual) {
        String[] expectedLines = expected.split("\n");
        String[] actualLines = actual.split("\n");
        int minLines = Math.min(expectedLines.length, actualLines.length);
        for (int i = 0; i < minLines; i++) {
            if (expectedLines[i].equals(actualLines[i])) continue;
            int minLen = Math.min(expectedLines[i].length(), actualLines[i].length());
            for (int j = 0; j < minLen; j++) {
                if (expectedLines[i].charAt(j) != actualLines[i].charAt(j)) {
                    return "Line " + (i + 1) + ", Column " + (j + 1) + ": expected '" +
                            expectedLines[i].charAt(j) + "', but got '" + actualLines[i].charAt(j) + "'";
                }
            }
            if (expectedLines[i].length() < actualLines[i].length()) {
                return "Line " + (i + 1) + ", Column " + (minLen + 1) + ": expected nothing, but got '"
                        + actualLines[i].charAt(minLen) + "'";
            } else {
                return "Line " + (i + 1) + ", Column " + (minLen + 1) + ": expected '" +
                        expectedLines[i].charAt(minLen) + "', but got nothing";
            }
        }
        if (expectedLines.length < actualLines.length) {
            return "Line " + (minLines + 1) + ": expected EOF, but got a new line";
        } else if (expectedLines.length > actualLines.length) {
            return "Line " + (minLines + 1) + ": expected a new line, but got EOF";
        }
        return "Unknown Error";
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
