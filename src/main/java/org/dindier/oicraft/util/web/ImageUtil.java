package org.dindier.oicraft.util.web;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.dindier.oicraft.assets.exception.BadFileException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class ImageUtil {

    public static byte[] compressImage(byte[] in, long size) throws BadFileException {
        long srcSize = in.length;
        double accuracy = getAccuracy(srcSize / 1024);
        try {
            while (in.length > size * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(in);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(in.length);
                Thumbnails.of(inputStream)
                        .scale(accuracy)
                        .outputQuality(accuracy)
                        .toOutputStream(outputStream);
                in = outputStream.toByteArray();
            }
            log.info("Compressed image from {}KB to {}KB", srcSize / 1024, in.length / 1024);
        } catch (IOException e) {
            throw new BadFileException("压缩图片失败");
        }
        return in;
    }


    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else accuracy = 0.4;
        return accuracy;
    }
}
