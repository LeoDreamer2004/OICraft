package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.PostService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("postService")
@Slf4j
public class PostServiceImpl implements PostService {
    private PostDao postDao;
    private CommentDao commentDao;
    private UserServiceImpl userService;

    @Override
    public Post getPostById(int id) {
        return postDao.getPostById(id);
    }

    @Override
    public Post savePost(Post post) {
        log.info("Save post: {}", post.getTitle());
        if (post.getId() == 0) {
            // A new post
            User user = post.getAuthor();
            user.setExperience(user.getExperience() + 3);
            userService.updateUser(user);
        }
        return postDao.savePost(post);
    }

    @Override
    public void deletePost(Post post) {
        User user = post.getAuthor();
        user.setExperience(user.getExperience() - 3);
        userService.updateUser(user);
        postDao.deletePost(post);
        log.info("Delete post: {}", post.getTitle());
    }

    @Override
    public Comment getCommentById(int id) {
        return commentDao.getCommentById(id);
    }

    @Override
    public Comment saveComment(Comment comment) {
        log.info("User {} commented on post {}", comment.getAuthor().getName(), comment.getPost().getId());
        if (comment.getId() == 0) {
            // A new comment
            User user = comment.getAuthor();
            user.setExperience(user.getExperience() + 1);
            userService.updateUser(user);
        }
        return commentDao.saveComment(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentDao.deleteComment(comment);
        User user = comment.getAuthor();
        user.setExperience(user.getExperience() - 1);
        userService.updateUser(user);
        log.info("Deleted a comment on post {}", comment.getPost().getId());
    }

    @Override
    public boolean canDeletePost(User user, @NotNull Post post) {
        return user != null && (user.equals(post.getAuthor()) || user.isAdmin());
    }

    @Override
    public boolean canDeleteComment(User user, @NotNull Comment comment) {
        return user != null && (user.equals(comment.getAuthor()) || user.isAdmin()
                || user.equals(comment.getPost().getAuthor()));
    }

    @Autowired
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }
}
