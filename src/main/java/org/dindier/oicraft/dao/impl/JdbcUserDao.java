package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.dao.repository.UserRepository;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public class JdbcUserDao implements UserDao {
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);

    public static final int INTERMEDIATE_MIN_PASS_NUM = 10;
    public static final int ADVANCED_MIN_PASS_NUM = 20;
    public static final int EXPERT_MIN_PASS_NUM = 30;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        user.setGrade(User.Grade.BEGINNER);
        user = userRepository.save(user);
        logger.info("Create user: {} (id: {})", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        int passedProblemsNum = getPassedProblemsByUserId(user.getId()).size();
        if (passedProblemsNum >= EXPERT_MIN_PASS_NUM) {
            user.setGrade(User.Grade.EXPERT);
        } else if (passedProblemsNum >= ADVANCED_MIN_PASS_NUM) {
            user.setGrade(User.Grade.ADVANCED);
        } else if (passedProblemsNum >= INTERMEDIATE_MIN_PASS_NUM) {
            user.setGrade(User.Grade.INTERMEDIATE);
        } else {
            user.setGrade(User.Grade.BEGINNER);
        }
        user = userRepository.save(user);
        logger.info("Update user: {} (id: {})", user.getName(), user.getId());
        return user;
    }

    @Override
    public boolean existsUser(String username) {
        return !userRepository.findByName(username).isEmpty();
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        List<User> users = userRepository.findByName(username);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<Problem> getTriedProblemsByUserId(int userId) {
        return userRepository
                .findById(userId)
                .map(User::getSubmissions)
                .map(submissions -> submissions
                        .stream()
                        .map(Submission::getProblem)
                        .toList()
                )
                .orElse(List.of());
    }

    @Override
    public List<Problem> getPassedProblemsByUserId(int userId) {
        return userRepository
                .findById(userId)
                .map(User::getSubmissions)
                .map(submissions -> submissions
                        .stream()
                        .filter(submission -> submission
                                .getCheckpoints()
                                .stream()
                                .allMatch(Checkpoint::isPassed))
                        .map(Submission::getProblem)
                        .toList()
                )
                .orElse(List.of());
    }

    @Override
    public List<Problem> getNotPassedProblemsByUserId(int userId) {
        return userRepository
                .findById(userId)
                .map(User::getSubmissions)
                .map(submissions -> submissions
                        .stream()
                        .filter(submission -> submission
                                .getCheckpoints()
                                .stream()
                                .anyMatch(checkpoint -> !checkpoint.isPassed()))
                        .map(Submission::getProblem)
                        .toList()
                )
                .orElse(List.of());
    }
}
