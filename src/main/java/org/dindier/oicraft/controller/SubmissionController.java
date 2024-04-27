package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.Submission;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SubmissionController {
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private CheckpointDao checkpointDao;
    private UserService userService;
    private ProblemService problemService;
    private HttpServletRequest request;

    @GetMapping("/submission/{id}")
    public ModelAndView submission(@PathVariable int id) {
        Submission submission = submissionDao.getSubmissionById(id);
        User user = userService.getUserByRequest(request);
        if (user == null) return null; // Actually, this page has been protected by interceptor
        if (submission == null) return new ModelAndView("error/404");

        Problem problem = problemDao.getProblemById(submission.getProblemId());
        if (problemService.hasPassed(user, problem) <= 0
                && !(user.isAdmin())
                && !(user.equals(submission.getUser()))) {
            return new ModelAndView("submission/notAllowed")
                    .addObject("problem", problemDao.getProblemById(submission.getProblemId()));
        }

        return new ModelAndView("submission/submission")
                .addObject("submission", submission)
                .addObject("problem", problemDao.getProblemById(submission.getProblemId()))
                .addObject("checkpoints", checkpointDao.getCheckpointsBySubmissionId(id));
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
    public void setCheckpointDao(CheckpointDao checkpointDao) {
        this.checkpointDao = checkpointDao;
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
}
