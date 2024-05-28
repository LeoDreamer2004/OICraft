package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.Problem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
    @Query(value = "select * from problem limit ?1, ?2", nativeQuery = true)
    List<Problem> findProblemInRange(int start, int number);

    @Query(value = """
            select row_no
            from (select row_number() over (order by id) as row_no, id
                  from problem) as rni
            where id = ?1
            limit 1;""", nativeQuery = true)
    int getProblemIndex(int problemId);
}
