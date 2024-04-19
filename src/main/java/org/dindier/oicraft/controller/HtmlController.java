package org.dindier.oicraft.controller;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class HtmlController {

    private ProblemDao problemDao;
    private ProblemService problemService;
    private SubmissionDao submissionDao;

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

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("user", "Dindier");
        return "index";
    }

    @GetMapping("/problems")
    public String problems(Model model) {
        model.addAttribute("problems", problemDao.getProblems());
        return "problems";
    }

    @GetMapping("/problem/{id}")
    public String problem(@PathVariable int id, Model model) {
        model.addAttribute("problem", problemDao.getProblemById(id));
        model.addAttribute("samples", problemDao.getSamplesById(1));
        return "problem";
    }

    @GetMapping("/problem/{id}/submit")
    public String submit(@PathVariable int id, Model model) {
        model.addAttribute("problem", problemDao.getProblemById(id));
        return "submit";
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
    public String submission(@PathVariable int id, Model model) {
        Submission submission = submissionDao.getSubmissionById(id);

        model.addAttribute("submission", submission);
        model.addAttribute("problem", problemDao.getProblemById(submission.getProblemId()));
        return "submission";
    }
}

