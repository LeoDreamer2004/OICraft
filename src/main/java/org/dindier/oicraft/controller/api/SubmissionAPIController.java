package org.dindier.oicraft.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubmissionAPIController {
    UserService userService;
    SubmissionService submissionService;
    HttpServletRequest request;

    @GetMapping("/api/submission/ai")
    public ResponseEntity<String> getAIAdvice(@RequestParam("submission") int id) {
        Submission submission = submissionService.getSubmissionById(id);
        User user = userService.getUserByRequest(request);
        if (submission == null || user == null || !user.equals(submission.getUser()))
            return ResponseEntity.badRequest().body("Invalid request for AI token");
        String content = submission.getAdviceAI();
        return ResponseEntity.ok(content);
    }

    @PostMapping("/api/submission/ai")
    public ResponseEntity<String> postAIAdvice(@RequestParam("submission") int id) {
        Submission submission = submissionService.getSubmissionById(id);
        User user = userService.getUserByRequest(request);
        if (submission == null || user == null || !user.equals(submission.getUser()))
            return ResponseEntity.badRequest().body("Invalid request for AI token");
        String content = submissionService.getAIAdvice(submission).getAdviceAI();
        return ResponseEntity.ok(content);
    }

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Autowired
    private void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
