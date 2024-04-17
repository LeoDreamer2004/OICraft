package org.dindier.oicraft;

import org.dindier.oicraft.util.CodeChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class OiCraftApplicationTests {

	@Test
	void contextLoads() {
		try {
			CodeChecker.run("E:\\C++\\temp\\Release\\demo.exe");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
    }

}
