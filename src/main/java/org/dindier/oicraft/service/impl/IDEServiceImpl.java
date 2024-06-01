package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.assets.exception.CodeCheckerError;
import org.dindier.oicraft.service.IDEService;
import org.dindier.oicraft.util.code.CodeChecker;
import org.dindier.oicraft.util.code.CodeCheckerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("IDEService")
public class IDEServiceImpl implements IDEService {
    @Override
    public Object runCode(String code, String language, String input) {
        CodeChecker codeChecker;
        try {
            // ignore the expected output in codeChecker
            codeChecker = CodeCheckerFactory.getCodeChecker()
                    .setIO(code, language, input, null)
                    .setLimit(5000, 512 * 1024);
            codeChecker.test();
        } catch (CodeCheckerError e) {
            return Map.of("usage", "服务器异常");
        }
        String output = codeChecker.getOutput();
        if (output == null)
            output = "";
        String usage = switch (codeChecker.getStatus()) {
            case CE -> "编译错误";
            case RE -> "运行时错误";
            case TLE -> "超出时间限制";
            case MLE -> "超出内存限制";
            case UKE -> "未知错误";
            default -> codeChecker.getUsedTime() + "ms, " + codeChecker.getUsedMemory() + "KB";
        };
        return Map.of("output", output, "usage", usage);
    }
}
