package org.dindier.oicraft;

import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.util.code.CodeChecker;
import org.dindier.oicraft.util.code.CodeCheckerFactory;
import org.dindier.oicraft.util.code.lang.Status;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@SpringBootTest
public class DockerCodeCheckerTest {
    public String getCode(URL filePath) {
        try (FileReader codeFile = new FileReader(Objects.requireNonNull(filePath).getFile())) {
            BufferedReader buf = new BufferedReader(codeFile);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPythonTLE() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        codeChecker.setIO("while True: pass", "Python", "1 2", "3")
                .setLimit(1000, 128 * 1024).test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.TLE;
    }

    @Test
    void testCppPass() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/pass.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.AC;
    }

    @Test
    void testCppWrongAnswer() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/wrong_answer.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.WA;
    }

    @Test
    void testCppRuntimeError() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/runtime_error.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(5000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.RE;
    }

    @Test
    void testCppCompileError() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/compile_error.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.CE;
    }

    @Test
    void testJavaPass() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/pass.java"));
        codeChecker.setIO(code, "Java", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.AC;
    }

    @Test
    void testJavaWrongAnswer() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/wrong_answer.java"));
        codeChecker.setIO(code, "Java", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedMemory() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.WA;
    }

    @Test
    void testCPass() throws CodeCheckerError {
        CodeChecker codeChecker = CodeCheckerFactory.getDockerCodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/pass.c"));
        codeChecker.setIO(code, "C", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus() == Status.AC;
    }
}
