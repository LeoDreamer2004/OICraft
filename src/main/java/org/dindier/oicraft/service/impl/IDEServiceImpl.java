package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.service.IDEService;
import org.dindier.oicraft.util.code.CodeChecker;
import org.dindier.oicraft.util.code.LocalCodeChecker;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service("IDEService")
@Slf4j
public class IDEServiceImpl implements IDEService {
    @Override
    public Object runCode(String code, String language, String input) {
        CodeChecker localCodeChecker;
        try {
            // ignore the expected output in codeChecker
            localCodeChecker = new LocalCodeChecker().setIO(code, language, input, "").setLimit(5000, 512 * 1024);
            localCodeChecker.test();
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while running code", e);
            return "Error occurred while running code";
        }
        String output = localCodeChecker.getOutput();
        if (output == null)
            output = "";
        String usage = switch (localCodeChecker.getStatus()) {
            case "CE" -> "编译错误";
            case "RE" -> "运行时错误";
            case "TLE" -> "超出时间限制";
            case "MLE" -> "超出内存限制";
            default ->
                    localCodeChecker.getUsedTime() + "ms, " + localCodeChecker.getUsedMemory() + "KB";
        };
        return Map.of("output", output, "usage", usage);
    }
}
