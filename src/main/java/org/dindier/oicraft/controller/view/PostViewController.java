package org.dindier.oicraft.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.PostService;
import org.dindier.oicraft.service.ProblemService;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RequestMapping("/post")
@Controller
public class PostViewController {

    private UserService userService;
    private ProblemService problemService;
    private PostService postService;
    private HttpServletRequest request;

    @GetMapping("/{id}")
    public ModelAndView getPost(@PathVariable int id) {
        User user = userService.getUserByRequestOptional(request);
        Post post = postService.getPostById(id);
        List<Comment> comments = post.getComments();
        return new ModelAndView("post/post").addObject("post", post)
                .addObject("comments", comments)
                .addObject("canDeletePost", postService.canDeletePost(user, post))
                .addObject("canDeleteComment", comments.stream().
                        map(comment -> postService.canDeleteComment(user, comment)).toList());
    }

    @GetMapping("/list")
    public ModelAndView posts(@RequestParam int problem) {
        Problem p = problemService.getProblemById(problem);
        return new ModelAndView("post/list")
                .addObject("problem", p)
                .addObject("posts", p.getPosts());
    }

    @GetMapping("/new")
    public ModelAndView newPost(@RequestParam int problem) {
        Problem p = problemService.getProblemById(problem);
        return new ModelAndView("post/new")
                .addObject("problem", p);
    }

    @PostMapping("/new")
    public RedirectView createPost(@RequestParam("problemId") int id,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content) {
        Problem problem = problemService.getProblemById(id);
        User user = userService.getUserByRequest(request);
        Post post = new Post(title, content, problem, user);
        postService.savePost(post);
        if (title.trim().isEmpty())
            return new RedirectView("/post/new?problem=" + id + "&error");
        return new RedirectView("/post/list?problem=" + id);
    }

    @PostMapping("/delete")
    public RedirectView deletePost(@RequestParam("postId") int postId) {
        User user = userService.getUserByRequest(request);
        Post post = postService.getPostById(postId);
        postService.checkCanDeletePost(user, post);
        postService.deletePost(post);
        return new RedirectView("/post/list?problem=" + post.getProblem().getId());
    }

    @PostMapping("/comment/delete")
    public RedirectView deleteComment(@RequestParam("commentId") int commentId) {
        User user = userService.getUserByRequest(request);
        Comment comment = postService.getCommentById(commentId);
        postService.checkCanDeleteComment(user, comment);
        postService.deleteComment(comment);
        return new RedirectView("/post/" + comment.getPost().getId());
    }

    @PostMapping("/comment")
    public RedirectView postComment(@RequestParam("postId") int postId,
                                    @RequestParam("content") String content) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserByRequest(request);
        if (content.trim().isEmpty())
            return new RedirectView("/post/" + postId + "?error");
        Comment comment = new Comment(user, post, content);
        postService.saveComment(comment);
        return new RedirectView("/post/" + postId);
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
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
