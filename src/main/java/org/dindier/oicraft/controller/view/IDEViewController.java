package org.dindier.oicraft.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/ide")
@Controller
public class IDEViewController {
    @GetMapping
    public ModelAndView getIDE() {
        return new ModelAndView("ide/ide");
    }
}
