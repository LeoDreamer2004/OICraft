package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.impl.JpaCommentDao;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostController {

    private PostDao postDao;
    private UserServiceImpl userService;
    private HttpServletRequest request;
    private JpaCommentDao commentDao;

    @GetMapping("/post/{id}")
    public ModelAndView getPost(@PathVariable int id) {
        Post post = postDao.getPostById(id);
        return new ModelAndView("post/post").addObject("post", post);
    }

    @PostMapping("/post/comment")
    public RedirectView postComment(@RequestParam("postId") int postId,
                                    @RequestParam("content") String content) {
        Post post = postDao.getPostById(postId);
        User user = userService.getUserByRequest(request);
        if (post == null) return new RedirectView("error/404");
        if (user == null) return new RedirectView("/login");
        Comment comment = new Comment(user, post, content);
        commentDao.createComment(comment);
        return new RedirectView("/post/" + postId);
    }

    @Autowired
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setCommentDao(JpaCommentDao commentDao) {
        this.commentDao = commentDao;
    }
}
