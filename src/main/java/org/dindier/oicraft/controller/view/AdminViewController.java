package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/admin")
@Controller
public class AdminViewController {

    private UserService userService;
    private HttpServletRequest request;

    @GetMapping
    public ModelAndView admin() {
        return new ModelAndView("admin/admin")
                .addObject("users", userService.getAllUsers());
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        return new ModelAndView("admin/delete")
                .addObject("deleteUser", user);
    }

    @PostMapping("/delete/{id}")
    public Object deleteUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        userService.deleteUser(user);
        return new RedirectView("/admin");
    }

    @GetMapping("/upgrade/{id}")
    public ModelAndView upgradeUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        return new ModelAndView("admin/upgrade")
                .addObject("upgradeUser", user);
    }

    @PostMapping("/upgrade/{id}")
    public Object upgradeUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        user.setRole(User.Role.ADMIN);
        userService.updateUser(user);
        return new RedirectView("/admin");
    }

    @GetMapping("/downgrade/{id}")
    public ModelAndView downgradeUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        return new ModelAndView("admin/downgrade")
                .addObject("downgradeUser", user);
    }

    @PostMapping("/downgrade/{id}")
    public Object downgradeUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        userService.checkCanEditUserAuthentication(userService.getUserByRequest(request), user);
        user.setRole(User.Role.USER);
        userService.updateUser(user);
        return new RedirectView("/admin");
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
