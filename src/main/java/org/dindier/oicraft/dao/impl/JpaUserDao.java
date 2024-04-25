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
public class JpaUserDao implements UserDao {
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(JpaUserDao.class);

    private static final int INTERMEDIATE_MIN_EXP = 100;
    private static final int ADVANCED_MIN_EXP = 200;
    private static final int EXPERT_MIN_EXP = 300;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        user = userRepository.save(user);
        logger.info("Create user: {} (id: {})", user.getName(), user.getId());
        return user;
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        int experience = user.getExperience();
        if (experience > EXPERT_MIN_EXP) {
            user.setGrade(User.Grade.EXPERT);
        } else if (experience > ADVANCED_MIN_EXP) {
            user.setGrade(User.Grade.ADVANCED);
        } else if (experience > INTERMEDIATE_MIN_EXP) {
            user.setGrade(User.Grade.INTERMEDIATE);
        } else {
            user.setGrade(User.Grade.BEGINNER);
        }
        user = userRepository.save(user);
        logger.info("Update user: {} (id: {})", user.getName(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
        logger.info("Delete user: {} (id: {})", user.getName(), user.getId());
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
                .map(problems -> problems.stream().distinct().toList())
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
                .map(problems -> problems.stream().distinct().toList())
                .orElse(List.of());
    }

    @Override
    public List<Problem> getNotPassedProblemsByUserId(int userId) {
        // FIXME: This method is not implemented correctly?

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
                .map(problems -> problems.stream().distinct().toList())
                .orElse(List.of());
    }

    @Override
    public List<Problem> getToSolveProblemsByUserId(int userId) {
        // TODO: Implement this method

        return List.of();
    }

    @Override
    public User addExperience(User user, int experience) {
        user.setExperience(user.getExperience() + experience);
        int newExperience = user.getExperience();
        if (newExperience > EXPERT_MIN_EXP) {
            logger.info("User {} (id: {}) has reached expert level", user.getName(), user.getId());
            user.setGrade(User.Grade.EXPERT);
        } else if (newExperience > ADVANCED_MIN_EXP) {
            logger.info("User {} (id: {}) has reached advanced level", user.getName(), user.getId());
            user.setGrade(User.Grade.ADVANCED);
        } else if (newExperience > INTERMEDIATE_MIN_EXP) {
            logger.info("User {} (id: {}) has reached intermediate level", user.getName(), user.getId());
            user.setGrade(User.Grade.INTERMEDIATE);
        } else {
            user.setGrade(User.Grade.BEGINNER);
        }
        return userRepository.save(user);
    }

}
