package org.dindier.oicraft.util.code.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Code compiler for compiled language such as C/C++, Java, Rust, etc. in the local environment
 * <p>It will compile the code and return the error message if any
 *
 * @author Crl
 */
@Slf4j
public enum LocalCodeCompiler {
    JAVA("javac"),
    CPP("g++", "-o", "main", "-O2"),
    C("gcc", "-o", "main", "-O2");

    private final String compiler;
    private final String[] compileOption;

    /**
     * Constructor for CodeCompiler
     *
     * @param compiler      The compiler to use (e.g. "g++", "cl", etc.)
     * @param compileOption The compile options for the compiler.
     *                      For example, for g++, it can be {"-o", "main"};
     *                      for javac, it can be {"-d", "temp"}.
     */
    LocalCodeCompiler(String compiler, String... compileOption) {
        this.compiler = compiler;
        this.compileOption = compileOption;
    }

    /**
     * Compile the source file using the compiler and compile options configured in the constructor.
     *
     * @param sourceFile The source file to compile
     * @return The error message if the compilation failed, otherwise null
     */
    public String compile(File sourceFile, File workingDirectory) {
        try {
            if (!workingDirectory.exists() && !workingDirectory.mkdir()) {
                log.error("Failed to create new file folder for {}", sourceFile);
            }
            String[] args = new String[compileOption.length + 2];
            args[0] = compiler;
            args[1] = sourceFile.getAbsolutePath();
            System.arraycopy(compileOption, 0, args, 2, compileOption.length);
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(workingDirectory);
            Process p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0) {
                return new String(p.getErrorStream().readAllBytes());
            }
        } catch (Exception e) {
            log.error("Failed to compile {}", sourceFile);
        }
        return null;
    }
}
