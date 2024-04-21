package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("problemDao")
public class JdbcProblemDao implements ProblemDao {

    @Override
    public void createProblem(Problem problem) {
        // TODO: Implement this method


        // A temporary implementation for testing
        problem.setId(1);
    }

    @Override
    public Problem getProblemById(int id) {
        // TODO: Implement this method


        // A temporary implementation for testing
        Problem problem = new Problem("a+b 问题", "把两个数相加",
                "两个整数 $a$, $b$，以空格隔开",
                "一个整数 $n = a + b$",
                Problem.Difficulty.EASY, 1400, 256);
        problem.setId(1);
        return problem;
    }

    @Override
    public List<Problem> getProblemList() {
        // TODO: Implement this method


        // A temporary implementation for testing
        return List.of(getProblemById(1));
    }

    @Override
    public List<IOPair> getSamplesById(int id) {
        // TODO: Implement this method


        // A temporary implementation for testing
        return List.of(
                new IOPair(1, "1 2", "3", IOPair.SAMPLE, 10),
                new IOPair(1, "3 4", "7", IOPair.SAMPLE, 10)
        );
    }

    @Override
    public List<IOPair> getTestsById(int id) {
        return List.of();
    }
}
