package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.service.UserService;
import org.dindier.oicraft.util.IterableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;


@Controller
public class ProblemController {
    private UserDao userDao;
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private UserService userService;
    private ProblemService problemService;
    private SubmissionService submissionService;
    private HttpServletRequest request;

    @GetMapping("/problems")
    public ModelAndView problems() {
        Iterable<Problem> problems = problemDao.getProblemList();
        User user = userService.getUserByRequest(request);
        Iterable<Integer> hasPassed = IterableUtil.map(problems, problem -> problemService.hasPassed(user, problem));
        return new ModelAndView("problem/list")
                .addObject("problems", problems)
                .addObject("hasPassed", hasPassed);
    }

    @GetMapping("/problem/{id}")
    public ModelAndView problem(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        return new ModelAndView("problem/problem")
                .addObject("problem", problem)
                .addObject("samples", problemDao.getSamplesById(id))
                .addObject("author", userDao.getUserById(problem.getAuthorId()));
    }

    @GetMapping("/problem/new")
    public ModelAndView newProblem() {
        return new ModelAndView("problem/new");
    }

    @PostMapping("/problem/new")
    public RedirectView createProblem(@RequestParam String title,
                                      @RequestParam String description,
                                      @RequestParam String inputFormat,
                                      @RequestParam String outputFormat,
                                      @RequestParam String difficulty,
                                      @RequestParam int timeLimit,
                                      @RequestParam int memoryLimit) {
        Map<String, Problem.Difficulty> difficultyMap = Map.of(
                "easy", Problem.Difficulty.EASY,
                "medium", Problem.Difficulty.MEDIUM,
                "hard", Problem.Difficulty.HARD
        );
        User user = userService.getUserByRequest(request);
        if (user == null)
            return new RedirectView("/login");
        Problem problem = new Problem(user.getId(), title, description, inputFormat, outputFormat,
                difficultyMap.get(difficulty), timeLimit, memoryLimit);
        problemDao.createProblem(problem);
        return new RedirectView("/problem/" + problem.getId());
    }

    @GetMapping("/problem/{id}/submit")
    public ModelAndView submitCode(@PathVariable int id) {
        return new ModelAndView("problem/submit")
                .addObject("problem", problemDao.getProblemById(id));
    }

    @GetMapping("/problem/{id}/history")
    public ModelAndView history(@PathVariable int id) {
        Iterable<Submission> submissions = submissionDao.getSubmissionsByProblemId(id);
        Iterable<User> users = IterableUtil.map(submissions, submission -> userDao.getUserById(submission.getUserId()));
        return new ModelAndView("problem/submissionHistory")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("submissions", submissions)
                .addObject("users", users);
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
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Autowired
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}

