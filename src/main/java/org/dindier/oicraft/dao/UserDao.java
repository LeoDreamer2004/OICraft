package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.User;

public interface UserDao {
    void createUser(User user);

    User getUserById(int id);
}
