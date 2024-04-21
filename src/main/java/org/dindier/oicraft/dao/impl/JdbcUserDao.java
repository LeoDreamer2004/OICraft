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
    public boolean isAdmin(User user) {
        // TODO: Implement this method

        return true;
    }
}
