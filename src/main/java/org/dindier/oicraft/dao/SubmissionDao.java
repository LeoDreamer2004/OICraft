package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Submission;

import java.util.List;

public interface SubmissionDao {

    void createSubmission(Submission submission);

    Submission getSubmissionById(int id);

    List<Submission> getSubmissionsByProblemId(int problemId);
}
