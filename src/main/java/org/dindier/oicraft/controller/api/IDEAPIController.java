package org.dindier.oicraft.controller.api;

import org.dindier.oicraft.service.IDEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IDEAPIController {
    private IDEService ideService;

    @PostMapping("/api/ide/run")
    private ResponseEntity<Object> runCode(@RequestParam("code") String code,
                                           @RequestParam("input") String input,
                                           @RequestParam("language") String language) {
        Object object = ideService.runCode(code, language, input);
        return ResponseEntity.ok(object);
    }

    @Autowired
    public void setIdeService(IDEService ideService) {
        this.ideService = ideService;
    }
}
