package org.dindier.oicraft;

import org.dindier.oicraft.util.ai.AIAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AITest {
    @Autowired
    private AIAdapter aiAdapter;

    @Test
    public void testConnection() {
        try {
            String res = aiAdapter.requestAI("", "你好");
            System.out.println(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
