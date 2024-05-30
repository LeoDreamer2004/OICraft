package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RequestMapping("/profile")
@Controller
public class ProfileViewController {

    private UserService userService;
    private ProblemService problemService;
    private HttpServletRequest request;


    @GetMapping
    public RedirectView profile() {
        User user = userService.getUserByRequest(request);
        // Redirect to user profile with user id
        return new RedirectView("/profile/" + user.getId());
    }

    @GetMapping("/{id}")
    public ModelAndView profile(@PathVariable int id) {
        User user = userService.getUserById(id);
        // use 'seeUser' in case of conflict with 'user' in the interceptor
        return new ModelAndView("user/profile", "seeUser", user)
                .addObject("passed", problemService.getPassedProblems(user))
                .addObject("toSolve", problemService.getNotPassedProblems(user))
                .addObject("hasCheckedIn", userService.hasCheckedInToday(user));
    }

    @GetMapping("/edit")
    public ModelAndView editProfile() {
        userService.getUserByRequest(request);
        return new ModelAndView("user/editProfile");
    }

    @GetMapping("/edit/avatar")
    public ModelAndView editAvatar() {
        userService.getUserByRequest(request);
        return new ModelAndView("user/editAvatar");
    }

    @PostMapping("/edit/avatar")
    public RedirectView editAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        User user = userService.getUserByRequest(request);
        byte[] avatarData = avatar.getInputStream().readAllBytes();
        if (userService.saveUserAvatar(user, avatarData) != 0)
            return new RedirectView("/profile/edit/avatar?error");
        return new RedirectView("/profile/edit/avatar");
    }

    @GetMapping("/edit/avatar/delete")
    public RedirectView deleteAvatar() {
        User user = userService.getUserByRequest(request);
        userService.saveUserAvatar(user, null);
        return new RedirectView("/profile/edit/avatar");
    }

    @GetMapping("/edit/info")
    public ModelAndView editInfo() {
        userService.getUserByRequest(request);
        return new ModelAndView("user/editInfo");
    }

    @PostMapping("/edit/info")
    public RedirectView editInfo(@RequestParam("signature") String signature) {
        User user = userService.getUserByRequest(request);
        if (signature.length() <= 200) {
            user.setSignature(signature);
            userService.updateUser(user);
        }
        return new RedirectView("/profile");
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
