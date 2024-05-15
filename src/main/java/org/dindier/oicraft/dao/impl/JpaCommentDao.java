package org.dindier.oicraft.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.CommentDao;
import org.dindier.oicraft.dao.repository.CommentRepository;
import org.dindier.oicraft.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("commentDao")
@Slf4j
public class JpaCommentDao implements CommentDao {
    private final CommentRepository commentRepository;

    @Override
    public Comment getCommentById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Autowired
    public JpaCommentDao(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}
