package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
