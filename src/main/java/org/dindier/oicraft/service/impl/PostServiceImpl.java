package org.dindier.oicraft.service.impl;

import org.dindier.oicraft.model.Comment;
import org.dindier.oicraft.model.Post;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.PostService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service("postService")
public class PostServiceImpl implements PostService {
    @Override
    public boolean canDeletePost(User user, @NotNull Post post) {
        return user != null && (user.equals(post.getAuthor()) || user.isAdmin());
    }

    @Override
    public boolean canDeleteComment(User user, @NotNull Comment comment) {
        return user != null && (user.equals(comment.getAuthor()) || user.isAdmin()
                || user.equals(comment.getPost().getAuthor()));
    }
}
