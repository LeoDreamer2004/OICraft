package org.dindier.oicraft.util.code;

import org.dindier.oicraft.util.code.impl.DockerCodeChecker;
import org.dindier.oicraft.util.code.impl.LocalCodeChecker;

public class CodeCheckerFactory {
    public static CodeChecker getCodeChecker() {
        return CodeCheckerInitializer.useDocker ? getDockerCodeChecker() : getLocalCodeChecker();
    }

    public static CodeChecker getLocalCodeChecker() {
        return new LocalCodeChecker();
    }

    public static CodeChecker getDockerCodeChecker() {
        return new DockerCodeChecker();
    }
}
