package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class HtmlController {

    private ProblemDao problemDao;
    private ProblemService problemService;
    private SubmissionDao submissionDao;
    private CheckpointDao checkpointDao;
    private HttpServletRequest request;
    private UserService userService;

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Autowired
    public void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    public void setCheckpointDao(CheckpointDao checkpointDao) {
        this.checkpointDao = checkpointDao;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public ModelAndView index() {
        System.out.println(request.getRemoteUser());
        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("logout");
    }

    @GetMapping("/problems")
    public ModelAndView problems() {
        return new ModelAndView("problems")
                .addObject("problems", problemDao.getProblemList());
    }

    @GetMapping("/problem/{id}")
    public ModelAndView problem(@PathVariable int id) {
        return new ModelAndView("problem")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("samples", problemDao.getSamplesById(id));
    }

    @GetMapping("/problem/{id}/submit")
    public ModelAndView submitCode(@PathVariable int id) {
        return new ModelAndView("submitCode")
                .addObject("problem", problemDao.getProblemById(id));
    }

    @GetMapping("/problem/{id}/history")
    public ModelAndView history(@PathVariable int id) {
        return new ModelAndView("submitHistory")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("submissions", submissionDao.getSubmissionsByProblemId(id));
    }

    @PostMapping("/problem/{id}/result")
    public RedirectView handIn(@PathVariable int id,
                               @RequestParam("code") String code,
                               @RequestParam("language") String language,
                               Model model) {
        System.out.println("Language: " + language + "\nGet the code: " + code);
        model.addAttribute("problem", problemDao.getProblemById(id));

        int submissionId = problemService.testCode(id, code, language);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/submission/" + submissionId);
        return redirectView;
    }

    @GetMapping("/submission/{id}")
    public ModelAndView submission(@PathVariable int id) {
        Submission submission = submissionDao.getSubmissionById(id);
        return new ModelAndView("submission")
                .addObject("submission", submission)
                .addObject("problem", problemDao.getProblemById(submission.getProblemId()))
                .addObject("checkpoints", checkpointDao.getCheckpointsBySubmissionId(id));
    }


}

