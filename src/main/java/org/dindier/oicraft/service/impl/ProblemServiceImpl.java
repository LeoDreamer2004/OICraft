package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.springframework.stereotype.Service;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {
    @Override
    public int testCode(Problem problem, String code, String language) {
        // TODO: Implement this method

        return 1;
    }

    @Override
    public int hasPassed(User user, Problem problem) {
        // TODO: Implement this method

        return 1;
    }

    @Override
    public Iterable<Problem> getPassedProblems(User user) {
        // TODO: Implement this method

        return null;
    }
}
