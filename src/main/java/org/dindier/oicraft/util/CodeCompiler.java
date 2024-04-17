package org.dindier.oicraft.util;

import java.io.File;

/**
 * Code compiler for compiled language such as C/C++, Java, Rust, etc.
 * This class is used to compile source code file.
 */
public enum CodeCompiler {
    JAVA("javac"),
    CPP("g++", "-o", "main"),
    C("gcc", "-o", "main");

    private final String compiler;
    private final String[] compileOption;
    private static final File workingDirectory = new File(CodeChecker.FOLDER);

    /**
     * Constructor for CodeCompiler
     *
     * @param compiler         The compiler to use (e.g. "g++", "cl", etc.)
     * @param compileOption    The compile options for the compiler. For example, for g++, it can be {"-o", "main"}; for javac, it can be {"-d", "temp"}.
     */
    CodeCompiler(String compiler, String... compileOption) {
        this.compiler = compiler;
        this.compileOption = compileOption;
    }

    /**
     * Compile the source file using the compiler and compile options configured in the constructor.
     *
     * @param sourceFile The source file to compile
     * @return The error message if the compilation failed, otherwise null
     */
    public String compile(String sourceFile) {
        try {
            if (!workingDirectory.exists()) {
                throw new RuntimeException("No working directory");
            }
            String[] args = new String[compileOption.length + 2];
            args[0] = compiler;
            args[1] = sourceFile;
            System.arraycopy(compileOption, 0, args, 2, compileOption.length);
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(workingDirectory);
            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0) {
                return new String(p.getErrorStream().readAllBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
