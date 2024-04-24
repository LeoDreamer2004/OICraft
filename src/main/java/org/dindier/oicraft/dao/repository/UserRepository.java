package org.dindier.oicraft.dao.repository;

import org.dindier.oicraft.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Query("select u from User u where u.name = ?1")
    List<User> findByName(String name);
}
