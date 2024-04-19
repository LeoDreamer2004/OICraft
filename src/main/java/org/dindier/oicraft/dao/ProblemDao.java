package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Problem;
import org.springframework.stereotype.Repository;

public interface ProblemDao {
    Problem getProblemById(int id);
}
