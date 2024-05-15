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
    public Comment createComment(Comment comment) {
        log.info("User {} commented on post {}", comment.getAuthor().getName(), comment.getPost().getId());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        log.info("Deleted a comment on post {}", comment.getPost().getId());
        commentRepository.delete(comment);
    }

    @Autowired
    public JpaCommentDao(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}
