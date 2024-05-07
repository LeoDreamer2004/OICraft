package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AccountController {

    private UserDao userDao;
    private UserService userService;
    private HttpServletRequest request;

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("/index");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("user/register");
    }

    @PostMapping("/register")
    public RedirectView register(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        if (userDao.existsUser(username))
            return new RedirectView("/register?error");
        User user = new User(username, password,
                User.Role.USER, User.Grade.BEGINNER);
        user = userService.createUser(user);
        return new RedirectView("/register/success/" + user.getId());
    }

    @GetMapping("/register/success/{id}")
    public ModelAndView registerSuccess(@PathVariable int id) {
        return new ModelAndView("user/registerSuccess")
                .addObject("user", userDao.getUserById(id));
    }

    @GetMapping("/email")
    public ModelAndView email() {
        return new ModelAndView("user/email");
    }

    @PostMapping("/email")
    public Object setEmail(@RequestParam("email") String email,
                           @RequestParam("code") String code) {
        User user = userService.getUserByRequest(request);
        if (user == null || !userService.verifyEmail(user, email, code))
            return new RedirectView("/email?error");
        user.setEmail(email);
        userDao.updateUser(user);
        return new ModelAndView("user/emailSuccess");
    }

    @GetMapping("/email/verification/new")
    public ResponseEntity<String> getVerificationForNew(@PathParam("email") String email) {
        User user = userService.getUserByRequest(request);
        if (user == null)
            return ResponseEntity.badRequest().body("Not logged in");
        userService.sendVerificationCode(user, email);
        return ResponseEntity.ok("Get verification");
    }

    @GetMapping("/email/verification/reset")
    public Object getVerificationForReset(@PathParam("username") String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null || user.getEmail() == null)
            return ResponseEntity.badRequest().body("User or email not found");
        userService.sendVerificationCode(user, user.getEmail());
        return ResponseEntity.ok("Get verification");
    }

    @GetMapping("/password/forget")
    public ModelAndView forgetPassword() {
        return new ModelAndView("user/forgetPassword");
    }

    @PostMapping("/password/forget")
    public Object forgetPassword(
            @RequestParam("username") String username,
            @RequestParam("code") String code) {
        User user = userDao.getUserByUsername(username);
        if (user == null || !userService.verifyEmail(user, user.getEmail(), code)) {
            return new RedirectView("/password/forget?error");
        }
        // password for security
        return new ModelAndView("user/resetPassword").
                addObject("username", username).
                addObject("originalPassword", user.getPassword());
    }

    @GetMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("originalPassword") String originalPassword) {
        // The original password parameter is from forget page, which served as a key
        User user = userDao.getUserByUsername(username);
        if (user == null || !user.getPassword().equals(originalPassword))
            // illegal access
            return ResponseEntity.badRequest().body("Illegal access");
        user.setPassword(userService.encodePassword(password));
        userDao.updateUser(user);
        return ResponseEntity.ok().body("Password reset");
    }

    @GetMapping("/password/reset/success")
    public ModelAndView resetPasswordSuccess() {
        return new ModelAndView("user/resetSuccess");
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("user/logout");
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
