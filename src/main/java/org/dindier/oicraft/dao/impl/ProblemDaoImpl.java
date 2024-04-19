package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.model.Problem;
import org.springframework.stereotype.Repository;

@Repository("problemDao")
public class ProblemDaoImpl implements ProblemDao {
    @Override
    public Problem getProblemById(int id) {
        return null;
    }
}
