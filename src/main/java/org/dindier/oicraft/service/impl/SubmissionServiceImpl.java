package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("submissionService")
public class SubmissionServiceImpl implements SubmissionService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getUser(Submission submission) {
        return userDao.getUserById(submission.getUser().getId());
    }
}
