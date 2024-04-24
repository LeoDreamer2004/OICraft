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
        String username = request.getRemoteUser();
        if (username == null)
            return null;
        return userDao.getUserByUsername(username);
    }

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}