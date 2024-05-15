package org.dindier.oicraft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.dao.ProblemDao;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.PostService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class PostController {

    private ProblemDao problemDao;
    private PostDao postDao;
    private CommentDao commentDao;
    private UserService userService;
    private HttpServletRequest request;
    private PostService postService;

    @GetMapping("/post/{id}")
    public ModelAndView getPost(@PathVariable int id) {
        User user = userService.getUserByRequest(request);
        Post post = postDao.getPostById(id);
        if (post == null) return new ModelAndView("error/404");
        List<Comment> comments = post.getComments();
        return new ModelAndView("post/post").addObject("post", post)
                .addObject("comments", comments)
                .addObject("canDeletePost", postService.canDeletePost(user, post))
                .addObject("canDeleteComment", comments.stream().
                        map(comment -> postService.canDeleteComment(user, comment)).toList());
    }

    @GetMapping("/problem/{id}/posts")
    public ModelAndView posts(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        return new ModelAndView("post/list")
                .addObject("problem", problem);
    }

    @GetMapping("/problem/{id}/post/new")
    public ModelAndView newPost(@PathVariable int id) {
        Problem problem = problemDao.getProblemById(id);
        if (problem == null)
            return new ModelAndView("error/404");
        return new ModelAndView("post/new")
                .addObject("problem", problem);
    }

    @PostMapping("/problem/{id}/post/new")
    public RedirectView createPost(@PathVariable int id,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content) {
        Problem problem = problemDao.getProblemById(id);
        if (problem == null)
            return new RedirectView("error/404");
        User user = userService.getUserByRequest(request);
        if (user == null)
            return new RedirectView("/login");
        Post post = new Post(title, content, problem, user);
        postDao.createPost(post);
        return new RedirectView("/problem/" + id + "/posts");
    }

    @PostMapping("/post/delete")
    public RedirectView deletePost(@RequestParam("postId") int postId) {
        User uer = userService.getUserByRequest(request);
        Post post = postDao.getPostById(postId);
        if (post == null)
            return new RedirectView("error/404");
        if (!postService.canDeletePost(uer, post))
            return new RedirectView("error/403");
        postDao.deletePost(post);
        return new RedirectView("/problem/" + post.getProblem().getId() + "/posts");
    }

    @PostMapping("/post/comment/delete")
    public RedirectView deleteComment(@RequestParam("commentId") int commentId) {
        User user = userService.getUserByRequest(request);
        Comment comment = commentDao.getCommentById(commentId);
        if (comment == null)
            return new RedirectView("error/404");
        if (!postService.canDeleteComment(user, comment))
            return new RedirectView("error/403");
        commentDao.deleteComment(comment);
        return new RedirectView("/post/" + comment.getPost().getId());
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
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
