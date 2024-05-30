package org.dindier.oicraft;

import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.util.code.impl.DockerCodeCompiler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DockerCodeCompilerTest {
    static void deleteDockerContainer(String containerName) {
        try {
            Process process = new ProcessBuilder("docker", "rm", containerName).start();
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete docker container " + containerName);
        }
    }

    static void stopDockerContainer(String containerName) {
        try {
            Process process = new ProcessBuilder("docker", "stop", containerName).start();
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stop docker container " + containerName);
        }
    }

    void copyFile(File source, File dest) {
        try {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file");
        }
    }

    @BeforeAll
    static void deleteDockerContainers() {
        deleteDockerContainer("01");
        deleteDockerContainer("02");
        deleteDockerContainer("03");
    }

    @AfterAll
    static void stopDockerContainers() {
        Thread[] threads = new Thread[3];
        for (int i = 1; i <= 3; i++) {
            int finalI = i;
            threads[i - 1] = new Thread(() -> stopDockerContainer("0" + finalI));
            threads[i - 1].start();
        }
        try {
            for (int i = 1; i <= 3; i++) {
                threads[i - 1].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to stop docker containers");
        }
    }


    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileErrorCpp() throws CodeCheckerError {
        DockerCodeCompiler compiler = DockerCodeCompiler.CPP;
        URL cppFile = DockerCodeCompiler.class.getClassLoader().getResource("test_codes/compile_error.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        // copy the test code to a temp file called Main.cpp
        File cppFileTemp = new File("Main.cpp");
        copyFile(new File(cppFile.getFile()), cppFileTemp);
        String result = compiler.compile(cppFileTemp, "01", true);
        cppFileTemp.delete();
        // The test code has compile error, so the result should not be null
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileSuccessCpp() throws CodeCheckerError {
        DockerCodeCompiler compiler = DockerCodeCompiler.CPP;
        URL cppFile = DockerCodeCompiler.class.getClassLoader().getResource("test_codes/pass.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        // copy the test code to a temp file called Main.cpp
        File cppFileTemp = new File("Main.cpp");
        copyFile(new File(cppFile.getFile()), cppFileTemp);
        String result = compiler.compile(cppFileTemp, "02", true);
        cppFileTemp.delete();
        // The test code has no compile error, so the result should be null
        assertNull(result);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileSuccessJava() throws CodeCheckerError {
        DockerCodeCompiler compiler = DockerCodeCompiler.JAVA;
        URL javaFile = DockerCodeCompiler.class.getClassLoader().getResource("test_codes/pass.java");
        if (javaFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        // copy the test code to a temp file called Main.java
        File javaFileTemp = new File("Main.java");
        copyFile(new File(javaFile.getFile()), javaFileTemp);
        String result = compiler.compile(javaFileTemp, "03", true);
        javaFileTemp.delete();
        // The test code has compile error, so the result should not be null
        assertNull(result);
    }
}
