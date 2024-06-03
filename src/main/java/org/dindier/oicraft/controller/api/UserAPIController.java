package org.dindier.oicraft.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.assets.exception.BadFileException;
import org.dindier.oicraft.assets.exception.EmailVerificationError;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/user")
@RestController
public class UserAPIController {

    private UserService userService;
    private HttpServletRequest request;

    @PostMapping("/edit/avatar")
    public ResponseEntity<String> editAvatar(@RequestParam("avatar") MultipartFile avatar) {
        User user = userService.getUserByRequest(request);
        try {
            byte[] avatarData;
            try {
                avatarData = avatar.getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new BadFileException("读取文件流异常");
            }
            userService.saveUserAvatar(user, avatarData);
        } catch (BadFileException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/delete/avatar")
    public ResponseEntity<String> deleteAvatar() {
        User user = userService.getUserByRequest(request);
        try {
            userService.saveUserAvatar(user, null);
        } catch (BadFileException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping
    public ResponseEntity<Integer> getUserId(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResponseEntity.badRequest().body(-1);
        return ResponseEntity.ok(user.getId());
    }

    @PostMapping("/checkin")
    public ResponseEntity<String> checkin() {
        userService.checkIn(userService.getUserByRequest(request));
        return ResponseEntity.ok("Checkin");
    }

    @GetMapping("/email/new")
    public ResponseEntity<String> getVerificationForNew(@RequestParam String email) {
        User user = userService.getUserByRequest(request);
        try {
            userService.sendVerificationCode(user, email);
            return ResponseEntity.ok("ok");
        } catch (EmailVerificationError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/reset")
    public ResponseEntity<String> getVerificationForReset(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null || user.getEmail() == null)
            return ResponseEntity.badRequest().body("用户不存在或未绑定邮箱");
        try {
            userService.sendVerificationCode(user, user.getEmail());
        } catch (EmailVerificationError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/password/reset")
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
        return ResponseEntity.ok().body("ok");
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
