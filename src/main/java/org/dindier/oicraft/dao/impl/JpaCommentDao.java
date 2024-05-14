package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.dao.repository.CommentRepository;
import org.dindier.oicraft.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

@Repository("commentDao")
public class JpaCommentDao implements CommentDao {

    private final CommentRepository commentRepository;
    Logger logger = Logger.getLogger(JpaCommentDao.class.getName());

    @Autowired
    public JpaCommentDao(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
        logger.info("User " + comment.getUser().getName() + " commented on post " + comment.getPost().getId());
        return commentRepository.save(comment);
    }
}
