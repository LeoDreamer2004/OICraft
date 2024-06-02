package org.dindier.oicraft;

import org.dindier.oicraft.assets.exception.BadFileException;
import org.dindier.oicraft.util.web.ImageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class ImageTest {

    @Test
    public void compressTest() throws IOException, BadFileException {
        URL inURL = getClass().getClassLoader().getResource("test_img/img.jpg");
        assert inURL != null;
        File file = new File(inURL.getFile());
        try (InputStream in = new FileInputStream(file)) {
            byte[] out = ImageUtil.compressImage(in.readAllBytes(), 4);
            assert out.length < 4 * 1024;
            // 存储
            Files.write(Paths.get("src/test/resources/test_img/img_out.jpg"), out);
        }
    }
}
