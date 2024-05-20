package org.dindier.oicraft.util.code;

import org.springframework.stereotype.Component;

@Component
public class CodeCheckerFactory {
    public static CodeChecker getCodeChecker() {
        if (DockerInitializer.useDocker) {
            return getDockerCodeChecker();
        }
        return getLocalCodeChecker();
    }

    public static CodeChecker getLocalCodeChecker() {
        return new LocalCodeChecker();
    }

    public static CodeChecker getDockerCodeChecker() {
        return new DockerCodeChecker();
    }
}
