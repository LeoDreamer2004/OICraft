package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.dindier.oicraft.service.impl.ProblemServiceImpl;
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
public class UserController {

    private UserDao userDao;
    private UserService userService;
    private HttpServletRequest request;
    private ProblemServiceImpl problemService;

    @GetMapping("/")
    public ModelAndView index() {
        System.out.println(request.getRemoteUser());
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
        User user = new User(username, password, User.Role.USER, User.Grade.BEGINNER);
        System.out.println(username + " " + password);
        user = userService.createUser(user);
        return new RedirectView("/register/success/" + user.getId());
    }

    @GetMapping("/register/success/{id}")
    public ModelAndView registerSuccess(@PathVariable int id) {
        return new ModelAndView("user/registerSuccess")
                .addObject("user", userDao.getUserById(id));
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("user/logout");
    }

    @GetMapping("/profile")
    public RedirectView profile() {
        User user = userService.getUserByRequest(request);
        if (user == null)
            return new RedirectView("/login");
        // Redirect to user profile with user id
        return new RedirectView("/profile/" + user.getId());
    }

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable int id) {
        User user = userDao.getUserById(id);
        if (user == null)
            return new ModelAndView("error/404");
        // use 'seeUser' in case of conflict with 'user' in the interceptor
        return new ModelAndView("user/profile", "seeUser", user)
                .addObject("passed", problemService.getPassedProblems(user))
                .addObject("hasCheckedIn", userService.hasCheckedInToday(user));
    }

    @PostMapping("/checkin")
    public ResponseEntity<String> checkin() {
        userService.checkIn(userService.getUserByRequest(request));
        return ResponseEntity.ok("Checkin");
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
    public void setProblemService(ProblemServiceImpl problemService) {
        this.problemService = problemService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
