package org.dindier.oicraft;

import org.dindier.oicraft.util.code.LocalCodeChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.*;
import java.net.URL;
import java.util.Objects;

@SpringBootTest
public class LocalCodeCheckerTest {
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
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        localCodeChecker.setIO("while True: pass", "Python", "1 2", "3")
                .setLimit(1000, 128 * 1024).test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("TLE");
    }

    @Test
    void testCppPass() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/pass.cpp"));
        localCodeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("AC");
    }

    @Test
    void testCppWrongAnswer() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/wrong_answer.cpp"));
        localCodeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("WA");
    }

    @Test
    void testCppRuntimeError() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/runtime_error.cpp"));
        localCodeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(5000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("RE");
    }

    @Test
    void testCppCompileError() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/compile_error.cpp"));
        localCodeChecker.setIO(code, "C++", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("CE");
    }

    @Test
    void testJavaPass() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/pass.java"));
        localCodeChecker.setIO(code, "Java", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("AC");
    }

    @Test
    void testJavaWrongAnswer() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/wrong_answer.java"));
        localCodeChecker.setIO(code, "Java", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedMemory() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("WA");
    }

    @Test
    void testCPass() throws IOException, InterruptedException {
        LocalCodeChecker localCodeChecker = new LocalCodeChecker();
        String code = getCode(LocalCodeChecker.class.getClassLoader().getResource("test_codes/pass.c"));
        localCodeChecker.setIO(code, "C", "1 2", "3")
                .setLimit(1000, 128 * 1024)
                .test();
        System.out.println(localCodeChecker.getInfo() + "/" + localCodeChecker.getUsedTime() + "ms/" + localCodeChecker.getUsedMemory() + "KB");
        assert localCodeChecker.getStatus().equals("AC");
    }
}
