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
     * Save a post
     *
     * @param post The post to save
     * @return The saved post with data updated
     */
    Post savePost(Post post);

    /**
     * Delete a post
     *
     * @param post The post to delete
     * @implNote This method should also delete all the comments of the post
     */
    void deletePost(Post post);
}
