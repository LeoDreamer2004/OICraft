package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.Problem;
import org.springframework.data.repository.CrudRepository;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
}
