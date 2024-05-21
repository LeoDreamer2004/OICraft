package org.dindier.oicraft.controller.api;

import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPIController {

    UserService userService;

    @GetMapping("/api/user")
    public ResponseEntity<Integer> getUserId(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResponseEntity.badRequest().body(-1);
        System.out.println(user.getId() + user.getUsername());
        return ResponseEntity.ok(user.getId());
    }

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }
}
