package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.*;
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
import java.util.List;
import java.util.Map;


@Controller
public class ProblemController {
    private UserService userService;
    private ProblemService problemService;
    private IOPairService ioPairService;
    private HttpServletRequest request;

    @GetMapping("/problems")
    public ModelAndView problems() {
        User user = userService.getUserByRequest(request);
        String pageStr = request.getParameter("page");
        int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
        // Do NOT use problemService.hasPassed() here for optimization
        Map<Problem, Integer> problemMap = problemService.getProblemPageWithPassInfo(user, page);
        return new ModelAndView("problem/list")
                .addObject("problems", problemMap.keySet())
                .addObject("hasPassed", problemMap.values())
                .addObject("page", page)
                .addObject("totalPages", problemService.getProblemPages());
    }

    @GetMapping("/problems/search")
    public Object search(@RequestParam("keyword") String keyword) {
        if (keyword.isEmpty())
            // If the user input nothing, clear the filter and show all problems
            return new RedirectView("/problems");
        List<Problem> problems = problemService.searchProblems(keyword);
        User user = userService.getUserByRequest(request);
        List<Integer> hasPassed = problems.stream()
                .map(problem -> problemService.hasPassed(user, problem))
                .toList();
        return new ModelAndView("problem/list")
                .addObject("problems", problems)
                .addObject("hasPassed", hasPassed);
    }

    @GetMapping("/problem/{id}")
    public ModelAndView problem(@PathVariable int id) {
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        User user = userService.getUserByRequest(request);
        return new ModelAndView("problem/problem")
                .addObject("problem", problem)
                .addObject("samples", problemService.getSamples(problem))
                .addObject("author", problem.getAuthor())
                .addObject("canEdit", problemService.canEdit(user, problem))
                .addObject("historyScore", problemService.getHistoryScore(user, problem))
                .addObject("canSubmit", !problemService.getTests(problem).isEmpty());
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
        Problem problem = new Problem(user, title, description, inputFormat, outputFormat,
                Problem.Difficulty.fromString(difficulty), timeLimit, memoryLimit * 1024);
        problem = problemService.saveProblem(problem);
        return new RedirectView("/problem/" + problem.getId());
    }

    @GetMapping("/problem/{id}/submit")
    public ModelAndView submitCode(@PathVariable int id) {
        return new ModelAndView("problem/submit")
                .addObject("problem", problemService.getProblemById(id));
    }

    @GetMapping("/problem/{id}/download")
    public ResponseEntity<Object> download(@PathVariable int id) {
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return ResponseEntity.badRequest().body("No such problem");
        InputStreamSource inputStreamSource =
                new ByteArrayResource(problemService.getProblemMarkdown(problem).getBytes());
        return ResponseEntity.ok().header("Content-Disposition",
                        "attachment; filename=\"%s.md\"".formatted(problem.getIdString()))
                .body(inputStreamSource);
    }

    @PostMapping("/problem/result")
    public RedirectView handIn(@RequestParam("problemId") int id,
                               @RequestParam("code") String code,
                               @RequestParam("language") String language,
                               Model model) {
        Problem problem = problemService.getProblemById(id);
        model.addAttribute("problem", problem);

        int submissionId = problemService.testCode(userService.getUserByRequest(request), problem, code, language);
        return new RedirectView("/submission/" + submissionId);
    }

    @GetMapping("/problem/{id}/edit")
    public ModelAndView edit(@PathVariable int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        if (!problemService.canEdit(user, problem))
            return new ModelAndView("error/403");
        return new ModelAndView("problem/edit")
                .addObject("problem", problem);
    }

    @PostMapping("/problem/edit")
    public RedirectView editConfirm(@RequestParam("problemId") int id,
                                    @RequestParam("title") String title,
                                    @RequestParam("description") String description,
                                    @RequestParam("inputFormat") String inputFormat,
                                    @RequestParam("outputFormat") String outputFormat,
                                    @RequestParam("difficulty") String difficulty,
                                    @RequestParam("timeLimit") int timeLimit,
                                    @RequestParam("memoryLimit") int memoryLimit) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new RedirectView("error/404");
        if (!problemService.canEdit(user, problem))
            return new RedirectView("error/403");
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

    @GetMapping("/problem/{id}/delete")
    public ModelAndView delete(@PathVariable int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        if (!problemService.canEdit(user, problem))
            return new ModelAndView("error/403");
        return new ModelAndView("/problem/delete").
                addObject("problem", problem);
    }

    @PostMapping("/problem/delete")
    public RedirectView deleteConfirm(@RequestParam("problemId") int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new RedirectView("error/404");
        if (!problemService.canEdit(user, problem))
            return new RedirectView("error/403");
        problemService.deleteProblem(problem);
        return new RedirectView("/problems");
    }

    @GetMapping("/problem/{id}/edit/checkpoints")
    public ModelAndView editCheckpoints(@PathVariable int id) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        if (!problemService.canEdit(user, problem))
            return new ModelAndView("error/403");
        return new ModelAndView("/problem/editCheckpoints")
                .addObject("problem", problem);
    }

    @PostMapping("/problem/{id}/edit/checkpoints/file")
    public ModelAndView editCheckpointsConfirm(@PathVariable int id,
                                               @RequestParam("file") MultipartFile file) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        if (!problemService.canEdit(user, problem))
            return new ModelAndView("error/403");
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

    @PostMapping("/problem/{id}/edit/checkpoints/text")
    public RedirectView editCheckpointsConfirm(@PathVariable int id,
                                               @RequestParam("input") String input,
                                               @RequestParam("output") String output,
                                               @RequestParam("type") String type,
                                               @RequestParam("score") int score) {
        User user = userService.getUserByRequest(request);
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            return new RedirectView("error/404");
        if (!problemService.canEdit(user, problem))
            return new RedirectView("error/403");
        Map<String, IOPair.Type> typeMap = Map.of(
                "sample", IOPair.Type.SAMPLE,
                "test", IOPair.Type.TEST
        );
        IOPair ioPair = new IOPair(problem, input, output, typeMap.get(type), score);
        ioPairService.saveIOPair(ioPair);
        return new RedirectView("/problem/" + id);
    }


    @GetMapping("/problem/{id}/checkpoints/download")
    public ResponseEntity<byte[]> downloadCheckpoints(@PathVariable int id) throws IOException {
        InputStream inputStream = ioPairService.getIOPairsStream(id);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return ResponseEntity.ok().header("Content-Disposition",
                "attachment; filename=\"checkpoints.zip\"").body(bytes);
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

