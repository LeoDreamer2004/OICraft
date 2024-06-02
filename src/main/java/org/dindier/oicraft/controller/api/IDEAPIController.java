package org.dindier.oicraft.controller.api;

import org.dindier.oicraft.service.IDEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/ide/")
@RestController
public class IDEAPIController {
    private IDEService ideService;

    @ResponseBody
    @PostMapping("/run")
    private Object runCode(@RequestParam("code") String code,
                           @RequestParam("input") String input,
                           @RequestParam("language") String language) {
        return ideService.runCode(code, language, input);
    }

    @Autowired
    public void setIdeService(IDEService ideService) {
        this.ideService = ideService;
    }
}
