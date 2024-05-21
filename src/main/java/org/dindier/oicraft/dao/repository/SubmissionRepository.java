package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.Submission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    @Query(value = "select * from submission where problem_id = ?1 order by id desc limit ?2, ?3",
            nativeQuery = true)
    List<Submission> findSubmissionInRangeByProblemId(int problemId, int start, int count);

    @Query(value = "select * from submission where problem_id = ?1 and user_id = ?2 order by id desc limit ?3, ?4",
            nativeQuery = true)
    List<Submission> findSubmissionInRangeByProblemAndUserId(int problemId, int userId, int start,
                                                             int count);

    @Query(value = "select count(*) from submission where problem_id = ?1", nativeQuery = true)
    int countByProblemId(int problemId);

    @Query(value = "select count(*) from submission where problem_id = ?1 and user_id = ?2",
            nativeQuery = true)
    int countByProblemAndUserId(int problemId, int userId);
}
