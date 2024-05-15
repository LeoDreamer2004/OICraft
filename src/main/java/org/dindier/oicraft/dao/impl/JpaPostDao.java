package org.dindier.oicraft.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.dao.repository.CommentRepository;
import org.dindier.oicraft.dao.repository.PostRepository;
import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("postDao")
@Slf4j
public class JpaPostDao implements PostDao {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public JpaPostDao(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Post getPostById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public Post createPost(Post post) {
        log.info("User {} created a post on problem {}", post.getAuthor().getName(), post.getProblem().getId());
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Post post) {
        List<Comment> comments = post.getComments();
        commentRepository.deleteAll(comments);
        postRepository.delete(post);
        log.info("Deleted a post on problem {}", post.getProblem().getId());
    }
}
