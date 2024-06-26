package org.dindier.oicraft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.exception.NoAuthenticationError;
import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.assets.exception.EntityNotFoundException;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.PostService;
import org.dindier.oicraft.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service("postService")
@Slf4j
public class PostServiceImpl implements PostService {
    private PostDao postDao;
    private CommentDao commentDao;
    private UserService userService;

    @NotNull
    @Override
    public Post getPostById(int id) throws EntityNotFoundException {
        Post post = postDao.getPostById(id);
        if (post == null)
            throw new EntityNotFoundException(Post.class);
        return post;
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

    @NotNull
    @Override
    public Comment getCommentById(int id) throws EntityNotFoundException {
        Comment comment = commentDao.getCommentById(id);
        if (comment == null)
            throw new EntityNotFoundException(Comment.class);
        return comment;
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
    public void checkCanDeletePost(User user, @NonNull Post post) throws NoAuthenticationError {
        if (user == null)
            throw new NoAuthenticationError("未登录");
        if (!user.equals(post.getAuthor()) && !user.isAdmin())
            throw new NoAuthenticationError("仅作者或管理员可以删除帖子");
    }

    @Override
    public void checkCanDeleteComment(User user, @NonNull Comment comment) {
        if (user == null)
            throw new NoAuthenticationError("未登录");
        if (!user.equals(comment.getAuthor())
                && !user.equals(comment.getPost().getAuthor())
                && !user.isAdmin())
            throw new NoAuthenticationError("仅作者或管理员可以删除评论");
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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
