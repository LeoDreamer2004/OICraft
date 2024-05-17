package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IDEController {
    private HttpServletRequest request;

    @GetMapping("/ide")
    private ModelAndView getIDE() {
        return new ModelAndView("ide/ide");
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
