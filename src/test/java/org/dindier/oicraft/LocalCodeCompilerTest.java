package org.dindier.oicraft;

import org.dindier.oicraft.util.code.impl.LocalCodeCompiler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URL;

@SpringBootTest
public class LocalCodeCompilerTest {

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileError() {
        LocalCodeCompiler compiler = LocalCodeCompiler.CPP;
        URL cppFile = LocalCodeCompilerTest.class.getClassLoader().getResource("test_codes/compile_error.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        String result = compiler.compile(new File(cppFile.getPath()), new File("./.temp_codes"));
        // The test code didn't compile correctly, so the result should not be null
        assert result != null;
        System.out.println(result);
        assert !new File(".temp_codes/main.exe").exists();
        if (new File(".temp_codes/").exists()) {
            new File(".temp_codes/").delete();
        }
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testCodeCompilerCompileSuccess() {
        LocalCodeCompiler compiler = LocalCodeCompiler.CPP;
        URL cppFile = LocalCodeCompilerTest.class.getClassLoader().getResource("test_codes/helloworld.cpp");
        if (cppFile == null) {
            throw new RuntimeException("Cannot find the test code file");
        }
        String result = compiler.compile(new File(cppFile.getPath()), new File("./.temp_codes"));
        // The test code has no compile error, so the result should be null
        assert result == null;
        File main = new File(".temp_codes/main.exe");
        assert main.exists();
        main.delete();
        if (new File(".temp_codes/").exists()) {
            new File(".temp_codes/").delete();
        }
    }
}
