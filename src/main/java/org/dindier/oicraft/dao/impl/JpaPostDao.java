package org.dindier.oicraft.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.dao.repository.PostRepository;
import org.dindier.oicraft.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("postDao")
@Slf4j
public class JpaPostDao implements PostDao {
    private final PostRepository postRepository;

    @Autowired
    public JpaPostDao(PostRepository postRepository) {
        this.postRepository = postRepository;
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
        log.info("Deleted a post on problem {}", post.getProblem().getId());
        postRepository.delete(post);
    }
}
