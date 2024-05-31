package org.dindier.oicraft.service;

import org.dindier.oicraft.assets.exception.EntityNotFoundException;
import org.dindier.oicraft.assets.exception.NoAuthenticationError;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.springframework.lang.NonNull;

public interface PostService {

    /**
     * Get the post by id
     *
     * @param id The post id
     * @return The post with the given id
     * @throws EntityNotFoundException If the post not exists
     */
    @NonNull
    Post getPostById(int id) throws EntityNotFoundException;

    /**
     * Save a post
     *
     * @param post The post to save
     * @return The saved post with data updated
     */
    @SuppressWarnings("UnusedReturnValue")
    Post savePost(Post post);

    /**
     * Delete a post
     *
     * @param post The post to delete
     */
    void deletePost(Post post);

    /**
     * Get the comment by id
     *
     * @param id The comment id
     * @return The comment with the given id
     * @throws EntityNotFoundException If the comment not exists
     */
    @NonNull
    Comment getCommentById(int id) throws EntityNotFoundException;

    /**
     * Save a comment
     *
     * @param comment The comment to save
     * @return The saved comment with data updated
     */
    @SuppressWarnings("UnusedReturnValue")
    Comment saveComment(Comment comment);

    /**
     * Delete a comment
     *
     * @param comment The comment to delete
     */
    void deleteComment(Comment comment);

    /**
     * Check if a user can delete a post
     *
     * @param user The user
     * @param post The post
     * @return If the user is allowed to delete the post
     */
    default boolean canDeletePost(User user, @NonNull Post post) {
        try {
            checkCanDeletePost(user, post);
            return true;
        } catch (NoAuthenticationError e) {
            return false;
        }

    }

    /**
     * Check if a user can delete a post
     *
     * @param user The user
     * @param post The post
     * @throws NoAuthenticationError If the user is not allowed to delete the post
     */
    void checkCanDeletePost(User user, @NonNull Post post) throws NoAuthenticationError;

    /**
     * Check if a user can delete a comment
     *
     * @param user    The user
     * @param comment The comment
     * @return If the user is allowed to delete the comment
     */
    default boolean canDeleteComment(User user, @NonNull Comment comment) {
        try {
            checkCanDeleteComment(user, comment);
            return true;
        } catch (NoAuthenticationError e) {
            return false;
        }
    }

    /**
     * Check if a user can delete a comment
     *
     * @param user    The user
     * @param comment The comment
     * @throws NoAuthenticationError If the user is not allowed to delete the comment
     */
    void checkCanDeleteComment(User user, @NonNull Comment comment) throws NoAuthenticationError;
}
