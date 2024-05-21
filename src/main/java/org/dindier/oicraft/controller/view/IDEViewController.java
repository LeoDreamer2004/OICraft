package org.dindier.oicraft.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IDEViewController {
    @GetMapping("/ide")
    private ModelAndView getIDE() {
        return new ModelAndView("ide/ide");
    }
}
