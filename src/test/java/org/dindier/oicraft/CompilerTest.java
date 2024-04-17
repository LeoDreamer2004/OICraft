package org.dindier.oicraft;

import org.dindier.oicraft.util.CodeCompiler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URL;

@SpringBootTest
public class CompilerTest {
    public String[] cppCompileOption = {"-o", "main"};

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileError() {
        CodeCompiler compiler = CodeCompiler.CPP;
        URL cppFile = CompilerTest.class.getClassLoader().getResource("test_codes/compile_error.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        String result = compiler.compile(cppFile.getPath().substring(1));
        // The test code has compile error, so the result should not be null
        assert result != null;
        System.out.println(result);
        assert !new File("./temp/main.exe").exists();
        if (new File("./temp").exists()) {
            new File("./temp").delete();
        }
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileSuccess() {
        CodeCompiler compiler = CodeCompiler.CPP;
        URL cppFile = CompilerTest.class.getClassLoader().getResource("test_codes/helloworld.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        String result = compiler.compile(cppFile.getPath().substring(1));
        // The test code has no compile error, so the result should be null
        assert result == null;
        File main = new File("./temp/main.exe");
        assert main.exists();
        main.delete();
        if (new File("./temp").exists()) {
            new File("./temp").delete();
        }
    }
}
