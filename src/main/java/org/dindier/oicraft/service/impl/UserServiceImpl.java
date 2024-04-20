package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        // TODO: Implement this method
        String username = request.getRemoteUser();
        if (username == null)
            return null;

        // A temporary implementation
        return new User(1, username, "password", User.Role.USER, User.Grade.BEGINNER);
    }
}
