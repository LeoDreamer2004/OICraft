package org.dindier.oicraft.util.code;

import org.dindier.oicraft.util.code.impl.DockerCodeChecker;
import org.dindier.oicraft.util.code.impl.LocalCodeChecker;

/**
 * A factory class for creating code checkers
 *
 * @author LeoDreamer
 */
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
