package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.PostDao;
import org.dindier.oicraft.model.Post;
import org.springframework.stereotype.Repository;

@Repository("postDao")
public class JpaPostDao implements PostDao  {
    private final JpaProblemDao problemDao;
    private final JpaUserDao userDao;

    public JpaPostDao(JpaProblemDao problemDao, JpaUserDao userDao) {
        this.problemDao = problemDao;
        this.userDao = userDao;
    }

    @Override
    public Post getPostById(int id) {
        // TODO: Implement this method

        return new Post("你好", "$$ 5 + 3 = 8 $$", problemDao.getProblemById(1),
                userDao.getUserById(1));
    }

    @Override
    public void createPost(Post post) {
        // TODO: Implement this method
    }
}
