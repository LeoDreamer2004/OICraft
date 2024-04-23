package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dindier.oicraft.dao.UserDao;

@Service("userService")
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        // TODO: Implement this method
        String username = request.getRemoteUser();
        if (username == null)
            return null;

        // A temporary implementation
//        User user = new User(username, "password", User.Role.ADMIN, User.Grade.BEGINNER);
//        user.setId(1);
//        return user;
        return userDao.getUserByUsername(username);
    }

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}