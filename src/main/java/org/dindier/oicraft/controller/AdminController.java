package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private UserDao userDao;
    private UserService userService;
    private HttpServletRequest request;

    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("user/admin")
                .addObject("users", userDao.getAllUsers());
    }

    @GetMapping("admin/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        User user = userDao.getUserById(id);
        User currentUser = userService.getUserByRequest(request);
        if (currentUser == null)
            return null;
        if (user == null)
            return ResponseEntity.badRequest().body("此用户不存在！");
        if (currentUser.equals(user))
            return ResponseEntity.badRequest().body("不能删除自己！");
        return ResponseEntity.ok("确认删除用户 " + user.getName() + " 吗？");
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
