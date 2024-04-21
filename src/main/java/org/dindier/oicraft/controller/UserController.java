package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private HttpServletRequest request;

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

    @GetMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("user/logout");
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
