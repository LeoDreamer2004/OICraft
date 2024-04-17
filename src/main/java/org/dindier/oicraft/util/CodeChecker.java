package org.dindier.oicraft.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodeChecker {

    public static final String FOLDER = "_temp_codes/";
    String codePath;
    String language;
    String inputPath;
    String outputPath;
    String expectedOutput;
    int timeLimit;
    int memoryLimit;

    private static final Map<String, String> extensionsMap = Map.of(
            "Java", "java",
            "C", "c",
            "C++", "cpp",
            "Python", "py"
    );
    private int usedTime;
    private int usedMemory;
    private String status;
    private String info;

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
        createFile(this.inputPath, input);

        // output
        this.outputPath = FOLDER + submissionId + ".out";
        createFile(this.outputPath);
        expectedOutput = output;

        // error
        createFile(FOLDER + submissionId + ".err");
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


    // This is just for testing
    public static void run(String program) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(program);
        Process p = pb.start();
        // Watch memory usage
        long max = 0;
        long pid = p.pid();
        AtomicBoolean isRunning = new AtomicBoolean(true);
        p.onExit().thenAccept((exitCode) -> isRunning.set(false));
        while (isRunning.get()) {
            long mem = getProcessMemoryUsage(pid);
            max = Math.max(max, mem);
            System.out.println("Memory usage: " + mem / 1024 + "KB");
            Thread.sleep(500);
        }
    }

    public static native long getProcessMemoryUsage(long pid);
}
