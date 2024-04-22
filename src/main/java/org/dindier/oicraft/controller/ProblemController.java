package org.dindier.oicraft.controller;

import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.service.ProblemService;
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
public class ProblemController {
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private ProblemService problemService;

    @GetMapping("/problems")
    public ModelAndView problems() {
        return new ModelAndView("/problem/list")
                .addObject("problems", problemDao.getProblemList());
    }

    @GetMapping("/problem/{id}")
    public ModelAndView problem(@PathVariable int id) {
        return new ModelAndView("problem/problem")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("samples", problemDao.getSamplesByProblemId(id));
    }

    @GetMapping("/problem/new")
    public ModelAndView newProblem() {
        return new ModelAndView("problem/new");
    }

    @GetMapping("/problem/{id}/submit")
    public ModelAndView submitCode(@PathVariable int id) {
        return new ModelAndView("problem/submit")
                .addObject("problem", problemDao.getProblemById(id));
    }

    @GetMapping("/problem/{id}/history")
    public ModelAndView history(@PathVariable int id) {
        return new ModelAndView("problem/submissionHistory")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("submissions", submissionDao.getSubmissionsByProblemId(id));
    }

    @PostMapping("/problem/{id}/result")
    public RedirectView handIn(@PathVariable int id,
                               @RequestParam("code") String code,
                               @RequestParam("language") String language,
                               Model model) {
        System.out.println("Language: " + language + "\nGet the code: " + code);
        Problem problem = problemDao.getProblemById(id);
        model.addAttribute("problem", problem);

        int submissionId = problemService.testCode(problem, code, language);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/submission/" + submissionId);
        return redirectView;
    }

    @Autowired
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    @Autowired
    public void setSubmissionDao(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }
}

