package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AccountViewController {

    private UserService userService;
    private HttpServletRequest request;

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public Object login() {
        if (userService.getUserByRequestOptional(request) != null)
            return new RedirectView("/");
        return new ModelAndView("user/login");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("user/register");
    }

    @PostMapping("/register")
    public RedirectView register(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        if (userService.existsUser(username))
            return new RedirectView("/register?error");
        User user = userService.createUser(username, password);
        return new RedirectView("/register/success?id=" + user.getId());
    }

    @GetMapping("/register/success")
    public ModelAndView registerSuccess(@RequestParam int id) {
        return new ModelAndView("user/registerSuccess")
                .addObject("user", userService.getUserById(id));
    }

    @GetMapping("/email")
    public ModelAndView email() {
        return new ModelAndView("user/email");
    }

    @PostMapping("/email")
    public Object setEmail(@RequestParam("email") String email,
                           @RequestParam("code") String code) {
        User user = userService.getUserByRequest(request);
        if (!userService.verifyEmail(user, email, code))
            return new RedirectView("/email?error");
        user.setEmail(email);
        userService.updateUser(user);
        return new ModelAndView("user/emailSuccess");
    }

    @GetMapping("/password/forget")
    public ModelAndView forgetPassword() {
        return new ModelAndView("user/forgetPassword");
    }

    @PostMapping("/password/forget")
    public Object forgetPassword(
            @RequestParam("username") String username,
            @RequestParam("code") String code) {
        System.out.println(username + " " + code);
        User user = userService.getUserByUsername(username);
        if (user == null || !userService.verifyEmail(user, user.getEmail(), code)) {
            return new RedirectView("/password/forget?error");
        }
        // password for security
        return new ModelAndView("user/resetPassword").
                addObject("username", username).
                addObject("originalPassword", user.getPassword());
    }

    @GetMapping("/password/reset/success")
    public ModelAndView resetPasswordSuccess() {
        return new ModelAndView("user/resetSuccess");
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("user/logout");
    }

    @GetMapping("/logoff")
    public ModelAndView logoff() {
        return new ModelAndView("user/logoff");
    }

    @PostMapping("/logoff")
    public RedirectView logoffConfirm() {
        User user = userService.getUserByRequest(request);
        userService.deleteUser(user);
        return new RedirectView("/");
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
