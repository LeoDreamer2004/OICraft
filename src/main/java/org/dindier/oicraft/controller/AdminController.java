package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AdminController {

    private UserService userService;
    private HttpServletRequest request;

    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin/admin")
                .addObject("users", userService.getAllUsers());
    }

    @GetMapping("/admin/delete/{id}")
    public ModelAndView deleteUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        return new ModelAndView("admin/delete")
                .addObject("deleteUser", user);
    }

    @PostMapping("/admin/delete/{id}")
    public Object deleteUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        userService.deleteUser(user);
        return new RedirectView("/admin");
    }

    @GetMapping("admin/upgrade/{id}")
    public ModelAndView upgradeUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        return new ModelAndView("admin/upgrade")
                .addObject("upgradeUser", user);
    }

    @PostMapping("admin/upgrade/{id}")
    public Object upgradeUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null || user == null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        user.setRole(User.Role.ADMIN);
        userService.updateUser(user);
        return new RedirectView("/admin");
    }

    @GetMapping("admin/downgrade/{id}")
    public ModelAndView downgradeUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        return new ModelAndView("admin/downgrade")
                .addObject("downgradeUser", user);
    }

    @PostMapping("admin/downgrade/{id}")
    public Object downgradeUserConfirm(@PathVariable int id) {
        User user = userService.getUserById(id);
        String error = canEditUser(user);
        if (error != null || user == null)
            return new ModelAndView("admin/error")
                    .addObject("error", error);
        user.setRole(User.Role.USER);
        userService.updateUser(user);
        return new RedirectView("/admin");
    }

    /* Check if the user can be edited */
    private String canEditUser(User user) {
        User currentUser = userService.getUserByRequest(request);
        if (user == null)
            return "用户不存在";
        if (currentUser == null)
            return "请先登录";
        if (currentUser.equals(user))
            return "不能修改自己的账户";
        return null;
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
