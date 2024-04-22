package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class JdbcUserDao implements UserDao {
    @Override
    public void createUser(User user) {
        // TODO: Implement this method
    }

    @Override
    public User getUserById(int id) {
        return new User("user", "111", User.Role.ADMIN, User.Grade.BEGINNER);
    }
}
