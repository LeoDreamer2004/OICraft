package org.dindier.oicraft.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.springframework.lang.Nullable;

public interface UserService {
    /**
     * Get user by request
     * @param request The request to get user from
     * @return The user from the request. If not authenticated, return null
     */
    @Nullable
    User getUserByRequest(HttpServletRequest request);
}
