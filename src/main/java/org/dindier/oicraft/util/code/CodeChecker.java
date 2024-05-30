package org.dindier.oicraft.util.code;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.constant.ConfigConstants;
import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.util.code.lang.Language;
import org.dindier.oicraft.util.code.lang.Status;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The abstract class for checking code
 * <p>The code checker will compile the code and run it with the input data
 * and check if the output is correct and runs within the time and memory limit
 *
 * @author Crl, LeoDreamer
 */
@Slf4j
public abstract class CodeChecker {
    protected long id;
    protected String codePath;
    protected Language language;
    protected String inputData;
    protected String expectedOutput;
    protected int timeLimit = 0;
    protected int memoryLimit = 0;
    protected boolean compiled = false;
    protected File workingDirectory; // The working directory for the submission

    protected static final Map<Language, String> extensionsMap = Map.of(
            Language.JAVA, "java",
            Language.C, "c",
            Language.CPP, "cpp",
            Language.PYTHON, "py"
    );
    @Getter
    protected String output;
    @Getter
    protected int usedTime = 0;
    @Getter
    protected int usedMemory = 0;
    @Getter
    protected Status status = Status.P;
    @Getter
    protected String info = "";

    protected static native long getProcessMemoryUsage(long pid);

    protected CodeChecker() {
        this.id = System.currentTimeMillis();
    }

    static {
        // create the folder if not exists
        File folder = new File(ConfigConstants.TEST_FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder: " + ConfigConstants.TEST_FOLDER);
        }
    }

    /**
     * Set the IO files for the code to be checked
     * <p>Notice: if {@code output} is set to {@code null},
     * We will not test if the answer is correct
     *
     * @param code     The code to be checked
     * @param language The language of the code
     * @param input    The input string
     * @param output   The expected output
     * @return The CodeChecker itself
     * @implNote This method will deal with the difference of line separator on different platforms
     */
    public CodeChecker setIO(String code, String language, String input,
                             @Nullable String output) throws CodeCheckerError {
        this.language = Language.fromString(language);
        this.inputData = input;
        this.expectedOutput = output == null ? null : output.stripTrailing();
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
     *
     * @param clearFile Whether to clear the files after the test
     */
    public abstract void test(boolean clearFile) throws CodeCheckerError;

    /**
     * Run the code and check the result
     * Use getter to get the status, info etc
     */
    public void test() throws CodeCheckerError {
        test(true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected static File createAndWriteToFile(String path, String content) {
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
            log.error("Error occurred while creating or writing to file", e);
            return null;
        }
    }

    /* Return the difference between expected and actual output */
    protected static String checkDifference(String expected, String actual) {
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

    protected static void deleteFolder(File folder) {
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
