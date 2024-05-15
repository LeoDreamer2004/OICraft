package org.dindier.oicraft.service;

import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.jetbrains.annotations.NotNull;

public interface PostService {
    /**
     * Check if a user can delete a post
     * @param user The user
     * @param post The post
     * @return {@code true} if the user can delete the post, {@code false} otherwise
     */
    boolean canDeletePost(User user, @NotNull Post post);

    /**
     * Check if a user can delete a comment
     * @param user The user
     * @param comment The comment
     * @return {@code true} if the user can delete the comment, {@code false} otherwise
     */
    boolean canDeleteComment(User user, @NotNull Comment comment);
}
