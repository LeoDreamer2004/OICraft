package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.service.ProblemService;
import org.springframework.stereotype.Service;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {
    @Override
    public int testCode(int problemId, String code, String language) {
        // TODO: Implement this method
        return 1;
    }
}
