package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
}
