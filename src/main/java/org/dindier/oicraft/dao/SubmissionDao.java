package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;

import java.util.List;

public interface SubmissionDao {
    Submission createSubmission(Submission submission);

    Submission updateSubmission(Submission submission);

    Submission getSubmissionById(int id);

    Iterable<Submission> getSubmissionsByProblemId(int problemId);

    Iterable<Submission> getAllSubmissions();
}
