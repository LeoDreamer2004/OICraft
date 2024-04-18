package org.dindier.oicraft;

import org.dindier.oicraft.util.CodeChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.*;
import java.net.URL;
import java.util.Objects;

@SpringBootTest
public class CodeCheckerTest {
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
    void testPythonTLE() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        codeChecker.setIO("while True: pass", "Python", "1 2", "3", 1)
                .setLimit(1000, 128 * 1024).test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("TLE");
    }

    @Test
    void testCppPass() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/pass.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3", 2)
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("AC");
    }

    @Test
    void testCppWrongAnswer() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/wrong_answer.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3", 3)
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("WA");
    }

    @Test
    void testCppRuntimeError() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/runtime_error.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3", 4)
                .setLimit(5000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("RE");
    }

    @Test
    void testCppCompileError() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/compile_error.cpp"));
        codeChecker.setIO(code, "C++", "1 2", "3", 5)
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("CE");
    }

    @Test
    void testJavaPass() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        String code = getCode(CodeChecker.class.getClassLoader().getResource("test_codes/pass.java"));
        codeChecker.setIO(code, "Java", "1 2", "3", 6)
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(codeChecker.getInfo() + "/" + codeChecker.getUsedTime() + "ms/" + codeChecker.getUsedMemory() + "KB");
        assert codeChecker.getStatus().equals("AC");
    }
}
