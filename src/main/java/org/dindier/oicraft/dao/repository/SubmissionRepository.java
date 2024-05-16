package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.Submission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    @Query(value = "select * from submission where problem_id = ?1 limit ?2, ?3", nativeQuery = true)
    List<Submission> findSubmissionInRangeByProblemId(int problemId, int start, int count);

    @Query(value = "select count(*) from submission where problem_id = ?1", nativeQuery = true)
    int countByProblemId(int problemId);
}
