package org.dindier.oicraft;

import org.dindier.oicraft.util.CodeChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;

@SpringBootTest
public class CodeCheckerTest {

    @Test
    void testRunning() throws IOException, InterruptedException {
        CodeChecker codeChecker = new CodeChecker();
        codeChecker.setIO("while True: pass","Python", "1 2", "3", 1)
                .setLimit(1000, 128).test();
        assert codeChecker.getStatus().equals("TLE");
        // System.out.println(codeChecker.getStatus());
    }
}
