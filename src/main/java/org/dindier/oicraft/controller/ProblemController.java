package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.IOPairService;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


@Controller
public class ProblemController {
    private UserDao userDao;
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private UserService userService;
    private ProblemService problemService;
    private IOPairService ioPairService;
    private HttpServletRequest request;

    private static final Map<String, Problem.Difficulty> difficultyMap = Map.of(
            "easy", Problem.Difficulty.EASY,
            "medium", Problem.Difficulty.MEDIUM,
            "hard", Problem.Difficulty.HARD
    );

    @GetMapping("/problems")
    public ModelAndView problems() {
        User user = userService.getUserByRequest(request);
        Map<Problem, Integer> problemMap = problemService.getAllProblemWithPassInfo(user);
        return new ModelAndView("problem/list")
                .addObject("problems", problemMap.keySet())
                .addObject("hasPassed", problemMap.values());
    }

    @GetMapping("/problem/{id}")
    public ModelAndView problem(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        User user = userService.getUserByRequest(request);
        return new ModelAndView("problem/problem")
                .addObject("problem", problem)
                .addObject("samples", problemDao.getSamplesById(id))
                .addObject("author", userDao.getUserById(problem.getAuthorId()))
                .addObject("canEdit", canEdit(problem))
                .addObject("historyScore", problemService.getHistoryScore(user, problem))
                .addObject("canSubmit", !problem.getIoPairs().isEmpty());
    }

    @GetMapping("/problem/new")
    public ModelAndView newProblem() {
        return new ModelAndView("problem/new");
    }

    @PostMapping("/problem/new")
    public RedirectView createProblem(@RequestParam("title") String title,
                                      @RequestParam("description") String description,
                                      @RequestParam("inputFormat") String inputFormat,
                                      @RequestParam("outputFormat") String outputFormat,
                                      @RequestParam("difficulty") String difficulty,
                                      @RequestParam("timeLimit") int timeLimit,
                                      @RequestParam("memoryLimit") int memoryLimit) {

        User user = userService.getUserByRequest(request);
        if (user == null)
            return new RedirectView("/login");
        Problem problem = new Problem(user.getId(), title, description, inputFormat, outputFormat,
                difficultyMap.get(difficulty), timeLimit , memoryLimit * 1024);
        problem = problemDao.createProblem(problem);
        return new RedirectView("/problem/" + problem.getId());
    }

    @GetMapping("/problem/{id}/submit")
    public ModelAndView submitCode(@PathVariable int id) {
        return new ModelAndView("problem/submit")
                .addObject("problem", problemDao.getProblemById(id));
    }

    @GetMapping("/problem/{id}/download")
    public ResponseEntity<InputStreamSource> download(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        InputStreamSource inputStreamSource = new ByteArrayResource(problemService.getProblemMarkdown(problem));
        return ResponseEntity.ok().header("Content-Disposition",
                        "attachment; filename=\"%s.md\"".formatted(problem.getIdString()))
                .body(inputStreamSource);
    }

    @GetMapping("/problem/{id}/history")
    public ModelAndView history(@PathVariable int id) {
        Iterable<Submission> submissions = submissionDao.getSubmissionsByProblemId(id);
        return new ModelAndView("submission/history")
                .addObject("problem", problemDao.getProblemById(id))
                .addObject("submissions", submissions);
    }

    @PostMapping("/problem/{id}/result")
    public RedirectView handIn(@PathVariable int id,
                               @RequestParam("code") String code,
                               @RequestParam("language") String language,
                               Model model) {
        Problem problem = problemDao.getProblemById(id);
        model.addAttribute("problem", problem);

        int submissionId = problemService.testCode(userService.getUserByRequest(request), problem, code, language);
        return new RedirectView("/submission/" + submissionId);
    }

    @GetMapping("/problem/{id}/edit")
    public ModelAndView edit(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new ModelAndView("error/403");
        return new ModelAndView("problem/edit")
                .addObject("problem", problem);
    }

    @PostMapping("/problem/{id}/edit")
    public RedirectView editConfirm(@PathVariable int id,
                                    @RequestParam("title") String title,
                                    @RequestParam("description") String description,
                                    @RequestParam("inputFormat") String inputFormat,
                                    @RequestParam("outputFormat") String outputFormat,
                                    @RequestParam("difficulty") String difficulty,
                                    @RequestParam("timeLimit") int timeLimit,
                                    @RequestParam("memoryLimit") int memoryLimit) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new RedirectView("error/403");
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setInputFormat(inputFormat);
        problem.setOutputFormat(outputFormat);
        problem.setDifficulty(difficultyMap.get(difficulty));
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit * 1024);
        problemDao.updateProblem(problem);
        return new RedirectView("/problem/" + id);
    }

    @GetMapping("/problem/{id}/delete")
    public ModelAndView delete(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new ModelAndView("error/403");
        return new ModelAndView("/problem/delete").
                addObject("problem", problem);
    }

    @PostMapping("/problem/{id}/delete")
    public RedirectView deleteConfirm(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new RedirectView("error/403");
        problemDao.deleteProblem(problem);
        return new RedirectView("/problems");
    }

    @GetMapping("/problem/{id}/edit/checkpoints")
    public ModelAndView editCheckpoints(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new ModelAndView("error/403");
        return new ModelAndView("/problem/editCheckpoints")
                .addObject("problem", problem);
    }

    @PostMapping("/problem/{id}/edit/checkpoints")
    public ModelAndView editCheckpointsConfirm(@PathVariable int id,
                                               @RequestParam("file") MultipartFile file) {
        Problem problem = problemDao.getProblemById(id);
        if (!canEdit(problem))
            return new ModelAndView("error/403");
        System.out.println("here");
        String errorMsg = null;
        try {
            InputStream inputStream = file.getInputStream();
            if (ioPairService.addIOPairByZip(inputStream, problem.getId()) == -1)
                errorMsg = "创建失败！请检查文件格式！";
        } catch (IOException e) {
            errorMsg = "服务器内部文件流异常！\n" + e.getMessage();
        }

        if (errorMsg == null)
            return new ModelAndView("problem/list");
        return new ModelAndView("problem/editCheckpoints")
                .addObject("problem", problem)
                .addObject("errorMsg", errorMsg);
    }

    @GetMapping("/problem/{id}/checkpoints/download")
    public ResponseEntity<byte[]> downloadCheckpoints(@PathVariable int id) throws IOException {
        InputStream inputStream = ioPairService.getIOPairsStream(id);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return ResponseEntity.ok().header("Content-Disposition",
                "attachment; filename=\"checkpoints.zip\"").body(bytes);
    }


    private boolean canEdit(Problem problem) {
        User user = userService.getUserByRequest(request);
        return (user != null) && ((user.isAdmin()) || (user.getId() == problem.getAuthorId()));
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
    public void setIoPairService(IOPairService ioPairService) {
        this.ioPairService = ioPairService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}

