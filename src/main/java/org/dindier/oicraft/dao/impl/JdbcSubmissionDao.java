package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Submission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("submissionDao")
public class JdbcSubmissionDao implements SubmissionDao {
    @Override
    public void createSubmission(Submission submission) {

    }

    @Override
    public Submission getSubmissionById(int id) {
        // TODO: Implement this method


        // A temporary implementation for testing
        Submission submission = new Submission(1,
                "a, b = map(input().split())\nprint(a + b)", Submission.Language.PYTHON);
        submission.setStatus(Submission.Status.PASSED);
        return submission;
    }

    @Override
    public List<Submission> getSubmissionsByProblemId(int problemId) {
        return List.of(
                getSubmissionById(1)
        );
    }
}
