package org.dindier.oicraft.dao;

import jakarta.annotation.Nullable;
import org.dindier.oicraft.model.Post;

public interface PostDao {
    /**
     * Get the post by id
     *
     * @param id The post id
     * @return The post with the given id, or {@code null} if not found
     */
    @Nullable
    Post getPostById(int id);

    /**
     * Create a post
     *
     * @param post The post to create
     * @return The created post with data updated
     */
    Post createPost(Post post);

    /**
     * Delete a post
     * @param post The post to delete
     */
    void deletePost(Post post);
}
