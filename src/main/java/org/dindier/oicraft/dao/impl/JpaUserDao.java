package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.dao.repository.*;
import org.dindier.oicraft.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public class JpaUserDao implements UserDao {
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(JpaUserDao.class);
    private CheckpointRepository checkpointRepository;
    private SubmissionRepository submissionRepository;
    private ProblemRepository problemRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(int id) {
        // delete the user's submissions and related checkpoints
        User user = getUserById(id);
        if (user == null) return;
        List<Submission> submissions = user.getSubmissions();
        List<Checkpoint> checkpoints = submissions
                .stream()
                .map(Submission::getCheckpoints)
                .flatMap(List::stream)
                .toList();
        checkpointRepository.deleteAll(checkpoints);
        submissionRepository.deleteAll(submissions);

        // set the user's problem to null
        List<Problem> problems = user.getProblems();
        for (Problem problem : problems) {
            problem.setAuthor(null);
        }
        problemRepository.saveAll(problems);

        // set the user's post and comment to null
        List<Post> posts = user.getPosts();
        for (Post post : posts) {
            post.setAuthor(null);
        }
        List<Comment> comments = user.getComments();
        for (Comment comment : comments) {
            comment.setAuthor(null);
        }
        postRepository.saveAll(posts);
        commentRepository.saveAll(comments);

        userRepository.delete(user);
        logger.info("Delete user: {} (id: {})", user.getName(), user.getId());
    }

    @Override
    public boolean existsUser(String username) {
        return !userRepository.findByName(username).isEmpty();
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        List<User> users = userRepository.findByName(username);
        return users.isEmpty() ? null : users.get(0);
    }

    @Autowired
    public void setCheckpointRepository(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}
