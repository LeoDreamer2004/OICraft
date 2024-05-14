package org.dindier.oicraft.dao;

import jakarta.annotation.Nullable;
import org.dindier.oicraft.model.Post;

public interface PostDao {
    /**
     * Get the post by id
     * @param id The post id
     * @return post
     */
    @Nullable
    Post getPostById(int id);

    /**
     * Create a post
     * @param post The post to create
     */
    void createPost(Post post);
}
