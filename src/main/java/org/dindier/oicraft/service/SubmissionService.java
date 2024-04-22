package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;

public interface SubmissionService {

    User getUser(Submission submission);
}
