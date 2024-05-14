package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.dao.repository.PostRepository;
import org.dindier.oicraft.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("postDao")
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
        return postRepository.save(post);
    }
}
