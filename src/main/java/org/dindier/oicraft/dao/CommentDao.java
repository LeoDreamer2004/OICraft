package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Comment;

public interface CommentDao {
    /**
     * Create a comment
     * @param comment The comment to create
     */
    void createComment(Comment comment);
}
