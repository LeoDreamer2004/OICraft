package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RequestMapping("/problems")
@Controller
public class ListViewController {
    private UserService userService;
    private HttpServletRequest request;
    private ProblemService problemService;

    @GetMapping
    public Object problems() {
        User user = userService.getUserByRequest(request);
        String pageStr = request.getParameter("page");
        String from = request.getParameter("from");
        if (pageStr == null) {
            int page = from == null ? 1 : problemService.getProblemPageNumber(Integer.parseInt(from));
            return new RedirectView("/problems?page=" + page);
        }
        int page = Integer.parseInt(pageStr);
        // Do NOT use problemService.hasPassed() here for optimization
        Map<Problem, Integer> problemMap = problemService.getProblemPageWithPassInfo(user, page);
        return new ModelAndView("problem/list")
                .addObject("problems", problemMap.keySet())
                .addObject("hasPassed", problemMap.values())
                .addObject("page", page)
                .addObject("totalPages", problemService.getProblemPages());
    }

    @GetMapping("/search")
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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }
}
