package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

@Repository("commentDao")
public class JpaCommentDao implements CommentDao {

    Logger logger = Logger.getLogger(JpaCommentDao.class.getName());

    @Override
    public void createComment(Comment comment) {
        logger.info("User " + comment.getUser().getName() + " commented on post " + comment.getPost().getId());
        // TODO: Implement this method
    }
}
