package org.dindier.oicraft.service;

public interface IDEService {
    /**
     * Run code and get the result
     *
     * @param code The code to run
     * @param language The language of the code
     * @param input The input of the code
     * @return The result of the code
     */
    Object runCode(String code, String language, String input);
}
