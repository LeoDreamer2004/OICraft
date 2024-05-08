package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class ProfileController {

    private UserDao userDao;
    private UserService userService;
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
        User user = userDao.getUserById(id);
        if (user == null)
            return new ModelAndView("error/404");
        // use 'seeUser' in case of conflict with 'user' in the interceptor
        return new ModelAndView("user/profile", "seeUser", user)
                .addObject("passed", userDao.getPassedProblemsByUserId(user.getId()))
                .addObject("toSolve", userDao.getNotPassedProblemsByUserId(user.getId()))
                .addObject("hasCheckedIn", userService.hasCheckedInToday(user));
    }

    @GetMapping("/profile/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable int id) {
        User user = userDao.getUserById(id);
        if (user == null)
            return ResponseEntity.notFound().build();
        byte[] avatarData = userService.getUserAvatar(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
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
        if (avatarData.length > 20 * 1024 * 1024) { // 20MB
            // FIXME: error message
            return new RedirectView("/profile/edit/avatar");
        }
        user.setAvatar(avatarData); // temporarily, we give no limits here
        userDao.updateUser(user);
        return new RedirectView("/profile");
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
        if (signature.length() > 255) {
            // FIXME: error message
            return new RedirectView("/profile/edit/info");
        }
        user.setSignature(signature);
        userDao.updateUser(user);
        return new RedirectView("/profile");
    }

    @PostMapping("/checkin")
    public ResponseEntity<String> checkin() {
        userService.checkIn(userService.getUserByRequest(request));
        return ResponseEntity.ok("Checkin");
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
