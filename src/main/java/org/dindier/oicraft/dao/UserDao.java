package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.User;

public interface UserDao {
    void createUser(User user);

    boolean isAdmin(User user);
}
