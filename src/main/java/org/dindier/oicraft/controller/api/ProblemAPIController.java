package org.dindier.oicraft.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.IOPairService;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/api/problem")
@RestController
public class ProblemAPIController {
    private ProblemService problemService;
    private IOPairService ioPairService;
    private UserService userService;
    private HttpServletRequest request;

    @GetMapping("/download")
    public ResponseEntity<Object> download(@RequestParam int id) {
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return ResponseEntity.badRequest().body("No such problem");
        InputStreamSource inputStreamSource =
                new ByteArrayResource(problemService.getProblemMarkdown(problem).getBytes());
        return ResponseEntity.ok().header("Content-Disposition",
                        "attachment; filename=\"%s.md\"".formatted(problem.getIdString()))
                .body(inputStreamSource);
    }

    @GetMapping("/checkpoints/download")
    public ResponseEntity<byte[]> downloadCheckpoints(@RequestParam int id) throws IOException {
        Problem problem = problemService.getProblemById(id);
        User user = userService.getUserByRequest(request);
        if (problem == null || !problemService.canEdit(user, problem))
            return ResponseEntity.badRequest().body(null);
        InputStream inputStream = ioPairService.getIOPairsStream(problem);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return ResponseEntity.ok().header("Content-Disposition",
                "attachment; filename=\"checkpoints.zip\"").body(bytes);
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Autowired
    public void setIoPairService(IOPairService ioPairService) {
        this.ioPairService = ioPairService;
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
