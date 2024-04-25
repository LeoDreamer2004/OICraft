package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.dindier.oicraft.dao.UserDao;

import java.util.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (!user.isAdmin()) {
            user.setGrade(User.Grade.BEGINNER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.createUser(user);
    }

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (username == null)
            return null;
        return userDao.getUserByUsername(username);
    }

    @Override
    public void checkIn(User user) {
        logger.info("User " + user.getUsername() + " checked in today");
        // TODO: Implement this method

    }

    @Override
    public boolean hasCheckedInToday(User user) {
        // TODO: Implement this method

        return false;
    }
}