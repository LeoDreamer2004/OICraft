package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.SubmissionDao;
import org.dindier.oicraft.dao.UserDao;
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

    private UserDao userDao;
    private ProblemDao problemDao;
    private SubmissionDao submissionDao;
    private CheckpointDao checkpointDao;
    private UserService userService;
    private ProblemService problemService;
    private HttpServletRequest request;

    @GetMapping("/submission/{id}")
    public ModelAndView submission(@PathVariable int id) {
        Submission submission = submissionDao.getSubmissionById(id);

        // See if the user can visit
        // if (!submission.getUserId().equals(request.getRemoteUser())) {
        //     return new ModelAndView("error")
        //             .addObject("message", "You are not allowed to view this submission");
        // }
        User user = userService.getUserByRequest(request);
        Problem problem = problemDao.getProblemById(submission.getProblemId());
        if (!problemService.hasPassed(user, problem) && !userDao.isAdmin(user)) {
            return new ModelAndView("submissionNotAllowed");
        }

        return new ModelAndView("submission/submission")
                .addObject("submission", submission)
                .addObject("problem", problemDao.getProblemById(submission.getProblemId()))
                .addObject("checkpoints", checkpointDao.getCheckpointsBySubmissionId(id));
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
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
