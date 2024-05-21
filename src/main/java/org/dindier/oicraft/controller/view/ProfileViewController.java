package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
public class ProfileViewController {

    private UserService userService;
    private ProblemService problemService;
    private HttpServletRequest request;


    @GetMapping("/profile")
    public RedirectView profile() {
        User user = userService.getUserByRequest(request);
        if (user == null)
            return new RedirectView("/login");
        // Redirect to user profile with user id
        return new RedirectView("/profile/" + user.getId());
    }

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null)
            return new ModelAndView("error/404");
        // use 'seeUser' in case of conflict with 'user' in the interceptor
        return new ModelAndView("user/profile", "seeUser", user)
                .addObject("passed", problemService.getPassedProblems(user))
                .addObject("toSolve", problemService.getNotPassedProblems(user))
                .addObject("hasCheckedIn", userService.hasCheckedInToday(user));
    }

    @GetMapping("/profile/edit")
    public ModelAndView editProfile() {
        User user = userService.getUserByRequest(request);
        if (user == null) return new ModelAndView("error/404");
        return new ModelAndView("user/editProfile");
    }

    @GetMapping("/profile/edit/avatar")
    public ModelAndView editAvatar() {
        User user = userService.getUserByRequest(request);
        if (user == null) return new ModelAndView("error/404");
        return new ModelAndView("user/editAvatar");
    }

    @PostMapping("/profile/edit/avatar")
    public RedirectView editAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        User user = userService.getUserByRequest(request);
        if (user == null) return new RedirectView("/login");
        byte[] avatarData = avatar.getInputStream().readAllBytes();
        if (userService.saveUserAvatar(user, avatarData) != 0)
            return new RedirectView("/profile/edit/avatar?error");
        return new RedirectView("/profile/edit/avatar");
    }

    @GetMapping("/profile/edit/avatar/delete")
    public RedirectView deleteAvatar() {
        User user = userService.getUserByRequest(request);
        if (user == null) return new RedirectView("/login");
        userService.saveUserAvatar(user, null);
        return new RedirectView("/profile/edit/avatar");
    }

    @GetMapping("/profile/edit/info")
    public ModelAndView editInfo() {
        User user = userService.getUserByRequest(request);
        if (user == null) return new ModelAndView("error/404");
        return new ModelAndView("user/editInfo");
    }

    @PostMapping("/profile/edit/info")
    public RedirectView editInfo(@RequestParam("signature") String signature) {
        User user = userService.getUserByRequest(request);
        if (user == null) return new RedirectView("/login");
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
