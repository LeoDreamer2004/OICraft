package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class SubmissionController {
    private UserService userService;
    private ProblemService problemService;
    private SubmissionService submissionService;
    private HttpServletRequest request;

    @GetMapping("/problem/{id}/history")
    public ModelAndView history(@PathVariable int id) {
        Problem problem = problemService.getProblemById(id);
        String pageStr = request.getParameter("page");
        int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
        if (problem == null) return new ModelAndView("error/404");
        List<Submission> submissions = submissionService.getSubmissionsInPage(problem, page);
        return new ModelAndView("submission/history")
                .addObject("problem", problem)
                .addObject("submissions", submissions)
                .addObject("page", page)
                .addObject("totalPages", submissionService.getSubmissionPages(problem));
    }

    @GetMapping("/submission/{id}")
    public ModelAndView submission(@PathVariable int id) {
        Submission submission = submissionService.getSubmissionById(id);
        User user = userService.getUserByRequest(request);
        if (user == null) return null; // Actually, this page has been protected by interceptor
        if (submission == null) return new ModelAndView("error/404");

        Problem problem = submission.getProblem();
        if (problemService.hasPassed(user, problem) <= 0
                && !(user.isAdmin())
                && !(user.equals(submission.getUser()))) {
            return new ModelAndView("submission/notAllowed")
                    .addObject("problem", submission.getProblem());
        }

        return new ModelAndView("submission/submission")
                .addObject("submission", submission)
                .addObject("problem", submission.getProblem())
                .addObject("checkpoints", submission.getCheckpoints())
                .addObject("isAuthor", user.equals(submission.getUser()));
    }

    @PostMapping("submission/ai")
    public RedirectView AIAdvice(@RequestParam("submissionId") int id) {
        Submission submission = submissionService.getSubmissionById(id);
        User user = userService.getUserByRequest(request);

        if (submission == null || user == null || !user.equals(submission.getUser()))
            return new RedirectView("error/404");

        submissionService.getAIAdvice(submission);
        return new RedirectView("/submission/" + id);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
}
