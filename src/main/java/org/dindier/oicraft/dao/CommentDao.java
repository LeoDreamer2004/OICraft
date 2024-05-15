package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Comment;
import org.springframework.lang.Nullable;

public interface CommentDao {

    /**
     * Get the comment by id
     *
     * @param id The comment id
     * @return The comment with the given id, or {@code null} if not found
     */
    @Nullable
    Comment getCommentById(int id);

    /**
     * Create a comment
     *
     * @param comment The comment to create
     * @return The created comment with data updated
     */
    @SuppressWarnings("UnusedReturnValue")
    Comment createComment(Comment comment);

    /**
     * Delete a comment
     *
     * @param comment The comment to delete
     */
    void deleteComment(Comment comment);
}
