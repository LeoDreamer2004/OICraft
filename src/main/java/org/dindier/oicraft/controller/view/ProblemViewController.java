package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.assets.exception.BadFileException;
import org.dindier.oicraft.model.IOPair;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.IOPairService;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.SubmissionService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


@RequestMapping("/problem")
@Controller
public class ProblemViewController {
    private UserService userService;
    private ProblemService problemService;
    private IOPairService ioPairService;
    private HttpServletRequest request;
    private SubmissionService submissionService;

    @GetMapping("/{id}")
    public ModelAndView problem(@PathVariable int id) {
        Problem problem = problemService.getProblemById(id);
        User user = userService.getUserByRequestOptional(request);
        return new ModelAndView("problem/problem")
                .addObject("problem", problem)
                .addObject("samples", problemService.getSamples(problem))
                .addObject("author", problem.getAuthor())
                .addObject("canEdit", problemService.canEdit(user, problem))
                .addObject("canEditCheckpoints", problemService.canEditCheckpoints(user, problem))
                .addObject("historyScore", problemService.getHistoryScore(user, problem))
                .addObject("canSubmit", !problemService.getTests(problem).isEmpty());
    }

    @GetMapping("/new")
    public ModelAndView newProblem() {
        return new ModelAndView("problem/new");
    }

    @PostMapping("/new")
    public RedirectView createProblem(@RequestParam("title") String title,
                                      @RequestParam("description") String description,
                                      @RequestParam("inputFormat") String inputFormat,
                                      @RequestParam("outputFormat") String outputFormat,
                                      @RequestParam("difficulty") String difficulty,
                                      @RequestParam("timeLimit") int timeLimit,
                                      @RequestParam("memoryLimit") int memoryLimit) {
        if (title.trim().isEmpty())
            return new RedirectView("/problem/new?error");
        User user = userService.getUserByRequest(request);
        Problem problem = new Problem(user, title, description, inputFormat, outputFormat,
                Problem.Difficulty.fromString(difficulty), timeLimit, memoryLimit * 1024);
        problem = problemService.saveProblem(problem);
        return new RedirectView("/problem/" + problem.getId());
    }

    @GetMapping("/submit")
    public ModelAndView submitCode(@RequestParam int id) {
        return new ModelAndView("problem/submit")
                .addObject("problem", problemService.getProblemById(id));
    }


    @PostMapping("/result")
    public RedirectView handIn(@RequestParam("problemId") int id,
                               @RequestParam("code") String code,
                               @RequestParam("language") String language,
                               Model model) {
        Problem problem = problemService.getProblemById(id);
        model.addAttribute("problem", problem);

        int submissionId = problemService.testCode(userService.getUserByRequest(request), problem, code, language);
        return new RedirectView("/submission/" + submissionId);
    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEdit(user, problem);
        return new ModelAndView("problem/edit")
                .addObject("problem", problem);
    }

    @PostMapping("/edit")
    public RedirectView editConfirm(@RequestParam int id,
                                    @RequestParam String title,
                                    @RequestParam String description,
                                    @RequestParam String inputFormat,
                                    @RequestParam String outputFormat,
                                    @RequestParam String difficulty,
                                    @RequestParam int timeLimit,
                                    @RequestParam int memoryLimit) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEdit(user, problem);
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setInputFormat(inputFormat);
        problem.setOutputFormat(outputFormat);
        problem.setDifficulty(Problem.Difficulty.fromString(difficulty));
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit * 1024);
        problemService.saveProblem(problem);
        return new RedirectView("/problem/" + id);
    }

    @GetMapping("/delete")
    public ModelAndView delete(@RequestParam int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEdit(user, problem);
        return new ModelAndView("/problem/delete").
                addObject("problem", problem);
    }

    @PostMapping("/delete")
    public RedirectView deleteConfirm(@RequestParam int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEdit(user, problem);
        problemService.deleteProblem(problem);
        return new RedirectView("/problems");
    }

    @GetMapping("/edit/checkpoints")
    public ModelAndView editCheckpoints(@RequestParam int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEditCheckpoints(user, problem);
        return new ModelAndView("/problem/editCheckpoints")
                .addObject("problem", problem);
    }

    @PostMapping("/edit/checkpoints/file")
    public Object editCheckpointsConfirm(@RequestParam int id,
                                         @RequestParam("file") MultipartFile file) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        problemService.checkCanEditCheckpoints(user, problem);
        try {
            InputStream inputStream;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                throw new BadFileException("读取文件流异常");
            }
            ioPairService.addIOPairByZip(inputStream, id);
        } catch (BadFileException e) {
            return new ModelAndView("problem/editCheckpoints")
                    .addObject("problem", problem)
                    .addObject("error", e.getMessage());
        }
        return new RedirectView("/problems");

    }

    @PostMapping("/edit/checkpoints/text")
    public RedirectView editCheckpointsConfirm(@RequestParam int id,
                                               @RequestParam String input,
                                               @RequestParam String output,
                                               @RequestParam String type,
                                               @RequestParam int score) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (!problem.getSubmissions().isEmpty())
            return new RedirectView("error/404");
        problemService.checkCanEdit(user, problem);
        Map<String, IOPair.Type> typeMap = Map.of(
                "sample", IOPair.Type.SAMPLE,
                "test", IOPair.Type.TEST
        );
        IOPair ioPair = new IOPair(problem, input, output, typeMap.get(type), score);
        ioPairService.saveIOPair(ioPair);
        return new RedirectView("/problem/" + id);
    }

    @GetMapping("/history")
    public ModelAndView history(@RequestParam int id) {
        Problem problem = problemService.getProblemById(id);

        String pageStr = request.getParameter("page");
        int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
        String userId = request.getParameter("user");
        if (userId == null) userId = "all"; // Default value "all"
        User user = null;
        if (!userId.equals("all"))
            user = userService.getUserById(Integer.parseInt(userId));
        List<Submission> submissions = submissionService.getSubmissionsInPage(problem, page, user);
        return new ModelAndView("submission/history")
                .addObject("problem", problem)
                .addObject("userId", userId)
                .addObject("submissions", submissions)
                .addObject("page", page)
                .addObject("totalPages", submissionService.getSubmissionPages(problem, user));
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

    @Autowired
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
}

