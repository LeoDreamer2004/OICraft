package org.dindier.oicraft.util;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import oshi.util.FileUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodeChecker {

    public static final String FOLDER = "_temp_codes/";
    private String codePath;
    private String language;
    private String inputPath;
    private String outputPath;
    private String errorPath;
    private String expectedOutput;
    private int timeLimit = 0;
    private int memoryLimit = 0;

    private static final Map<String, String> extensionsMap = Map.of(
            "Java", "java",
            "C", "c",
            "C++", "cpp",
            "Python", "py"
    );
    private int usedTime = 0;
    private int usedMemory = 0;
    private String status = "P";
    private String info = "";

    private long startTime;
    private Process process;

    static {
        // Load the native library
        URL url = CodeChecker.class.getClassLoader().getResource("lib/CodeChecker.dll");
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
     * @param code The code to be checked
     * @param language The language of the code
     * @param input The input string
     * @param output The expected output
     * @param submissionId The id of the submission
     * @return The CodeChecker itself
     */
    public CodeChecker setIO(String code, String language, String input, String output, int submissionId) throws IOException {
        // code
        String extension = extensionsMap.get(language);
        if (extension == null)
            throw new IllegalArgumentException("Unsupported language: " + language);
        this.codePath = FOLDER + submissionId + "." + extension;
        createFile(this.codePath, code);

        // language
        this.language = language;

        // input
        this.inputPath = FOLDER + submissionId + ".in";
        createFile(this.inputPath, input + "\n"); // avoid EOF

        // output
        this.outputPath = FOLDER + submissionId + ".out";
        createFile(this.outputPath);
        expectedOutput = output.trim();  // omit the '\n'

        // error
        this.errorPath = FOLDER + submissionId + ".err";
        createFile(this.errorPath);
        return this;
    }

    /**
     * Set the time and memory limit for the code to be checked
     * @param timeLimit The time limit in milliseconds
     * @param memoryLimit The memory limit in KB
     * @return The CodeChecker itself
     */
    public CodeChecker setLimit(int timeLimit, int memoryLimit) {
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        return this;
    }

    public int getUsedTime() {
        return usedTime;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public String getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    private File createFile(String path) throws IOException {
        File file = new File(path);
        boolean b = file.createNewFile();
        if (!file.exists())
            throw new RuntimeException("Failed to create file: " + path);
        return file;
    }

    private File createFile(String path, String content) throws IOException {
        File file = createFile(path);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    private String readFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new RuntimeException("File not found: " + path);
        try (FileReader reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            return sb.toString();
        }
    }

    /**
     * Run the code and check the result
     * Use getter to get the status, info etc
     */
    public void test() throws IOException, InterruptedException {
        ProcessBuilder pb = getProcessBuilder();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerTask();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 100);

        if (pb == null) return;
        startTime = System.currentTimeMillis();
        process = pb.start();
        process.waitFor();
        timer.cancel();

        checkAnswer();
        // TODO: clear the files
    }

    /* Get the ProcessBuilder for the code to be checked */
    private ProcessBuilder getProcessBuilder() {
        final String redirection = " <" + inputPath + " >" + outputPath + " 2>" + errorPath;
        CodeCompiler compiler = null;
        String runCmd;

        switch (language) {
            case "Python" -> runCmd = "python " + codePath + redirection;
            case "Java" -> {
                compiler = CodeCompiler.JAVA;
                runCmd = "java -cp " + FOLDER + " " + codePath.substring(0, codePath.length() - 5) + redirection;
            }
            case "C++" -> {
                compiler = CodeCompiler.CPP;
                runCmd = FOLDER + "main.exe" + redirection;
            }
            case "C" -> {
                compiler = CodeCompiler.C;
                runCmd = FOLDER + "main.exe" + redirection;
            }
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        }

        // if the compiler is not null, compile the code
        if (compiler != null) {
            String compileError = compiler.compile(codePath);
            if (compileError != null) {
                status = "CE";
                info = compileError;
                return null;
            }
        }
        return new ProcessBuilder("cmd", "/c", runCmd);
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
            temp = (int) getProcessMemoryUsage(process.pid());
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
        String output = readFile(outputPath).trim();

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
                    return "Line " + (i + 1) + "Column " + (j + 1) + ": expected '" + expectedLines[i].charAt(j) + "', but got '" + actualLines[i].charAt(j) + "'";
                }
            }
            if (expectedLines[i].length() < actualLines[i].length()) {
                return "Line " + (i + 1) + "Column " + (minLen + 1) + ": expected nothing, but got '" + actualLines[i].charAt(minLen) + "'";
            } else {
                return "Line " + (i + 1) + "Column " + (minLen + 1) + ": expected '" + expectedLines[i].charAt(minLen) + "', but got nothing";
            }
        }
        if (expectedLines.length < actualLines.length) {
            return "Line " + (minLines + 1) + ": expected EOF, but got a new line";
        } else if (expectedLines.length > actualLines.length) {
            return "Line " + (minLines + 1) + ": expected a new line, but got EOF";
        }
        return "Unknown Error";
    }
}
