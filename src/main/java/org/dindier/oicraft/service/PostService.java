package org.dindier.oicraft.service;

import jakarta.annotation.Nullable;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.jetbrains.annotations.NotNull;

public interface PostService {

    /**
     * Get the post by id
     *
     * @param id The post id
     * @return The post with the given id, or {@code null} if not found
     */
    @Nullable
    Post getPostById(int id);

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
     * @return The comment with the given id, or {@code null} if not found
     */
    @Nullable
    Comment getCommentById(int id);

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
     * @return {@code true} if the user can delete the post, {@code false} otherwise
     */
    boolean canDeletePost(User user, @NotNull Post post);

    /**
     * Check if a user can delete a comment
     *
     * @param user    The user
     * @param comment The comment
     * @return {@code true} if the user can delete the comment, {@code false} otherwise
     */
    boolean canDeleteComment(User user, @NotNull Comment comment);
}
