package org.dindier.oicraft;

import org.dindier.oicraft.util.CodeChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;

@SpringBootTest
public class CodeCheckerTest {

    @Test
    void testRunning() {
        try {
            // CodeChecker.run("E:\\Scripts\\Java\\CollegeCourse\\Exercise\\OICraft\\src\\test\\java\\org\\dindier\\oicraft\\demo.exe");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
