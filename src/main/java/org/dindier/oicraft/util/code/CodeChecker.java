package org.dindier.oicraft.util.code;

import lombok.Getter;
import org.apache.tomcat.jni.LibraryNotFoundError;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public abstract class CodeChecker {
    public static final String FOLDER = "temp/";
    String codePath;
    String language;
    String inputData;
    String expectedOutput;
    int timeLimit = 0;
    int memoryLimit = 0;
    // The working directory for the submission
    File workingDirectory;

    static final String platform;

    static final Map<String, String> extensionsMap = Map.of(
            "Java", "java",
            "C", "c",
            "C++", "cpp",
            "Python", "py"
    );
    @Getter
    String output;
    @Getter
    int usedTime = 0;
    @Getter
    int usedMemory = 0;
    @Getter
    String status = "P";
    @Getter
    String info = "";

    static native long getProcessMemoryUsage(long pid);

    static {
        // Load the native library
        URL url;
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            url = LocalCodeChecker.class.getClassLoader().getResource("lib/CodeChecker.so");
            platform = "Linux";
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            url = LocalCodeChecker.class.getClassLoader().getResource("lib/CodeChecker.dll");
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
     * @param code     The code to be checked
     * @param language The language of the code
     * @param input    The input string
     * @param output   The expected output
     * @return The CodeChecker itself
     */
    public abstract CodeChecker setIO(String code, String language, String input, String output) throws IOException;

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
     *
     * @param clearFile Whether to clear the files after the test
     */
    public abstract void test(boolean clearFile) throws IOException, InterruptedException;

    /**
     * Run the code and check the result
     * Use getter to get the status, info etc
     */
    public void test() throws IOException, InterruptedException {
        test(true);
    }

    /* Return the difference between expected and actual output */
    static String checkDifference(String expected, String actual) {
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
}
