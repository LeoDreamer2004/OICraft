package org.dindier.oicraft.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPIController {

    UserService userService;
    HttpServletRequest request;

    @GetMapping("/api/user")
    public ResponseEntity<Integer> getUserId(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResponseEntity.badRequest().body(-1);
        return ResponseEntity.ok(user.getId());
    }

    @PostMapping("/api/user/checkin")
    public ResponseEntity<String> checkin() {
        userService.checkIn(userService.getUserByRequest(request));
        return ResponseEntity.ok("Checkin");
    }

    @GetMapping("/api/user/email/new")
    public ResponseEntity<String> getVerificationForNew(@RequestParam String email) {
        User user = userService.getUserByRequest(request);
        if (user == null)
            return ResponseEntity.badRequest().body("Not logged in");
        userService.sendVerificationCode(user, email);
        return ResponseEntity.ok("Get verification");
    }

    @GetMapping("/api/user/email/reset")
    public ResponseEntity<String> getVerificationForReset(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null || user.getEmail() == null)
            return ResponseEntity.badRequest().body("User or email not found");
        userService.sendVerificationCode(user, user.getEmail());
        return ResponseEntity.ok("Get verification");
    }

    @PostMapping("/api/user/password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String originalPassword) {
        // The original password parameter is from forget page, which served as a key
        User user = userService.getUserByUsername(username);
        if (user == null || !user.getPassword().equals(originalPassword))
            // illegal access
            return ResponseEntity.badRequest().body("Illegal access");
        user.setPassword(userService.encodePassword(password));
        userService.updateUser(user);
        return ResponseEntity.ok().body("Password reset");
    }

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
