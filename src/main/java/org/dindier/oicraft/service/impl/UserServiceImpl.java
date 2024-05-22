package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.dindier.oicraft.util.email.EmailVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.dindier.oicraft.dao.UserDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.*;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private EmailVerifier emailVerifier;

    private static final int INTERMEDIATE_MIN_EXP = 100;
    private static final int ADVANCED_MIN_EXP = 200;
    private static final int EXPERT_MIN_EXP = 300;


    @Getter
    public static class VerificationCode {
        private final String code;
        private final long timestamp;

        public VerificationCode(String code) {
            this.code = code;
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailVerifier(EmailVerifier emailVerifier) {
        this.emailVerifier = emailVerifier;
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public User getUserByUsername(String name) {
        return userDao.getUserByUsername(name);
    }

    @Override
    public User loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = getUserByUsername(name);
        if (user == null)
            throw new UsernameNotFoundException("User " + name + " not found");
        return user;
    }

    @Override
    public User createUser(String username, String password) {
        User user = new User(username, passwordEncoder.encode(password),
                User.Role.USER, User.Grade.BEGINNER);
        user.setExperience(0);
        log.info("Created user {}", username);
        return userDao.saveUser(user);
    }

    @Override
    public void deleteUser(User user) {
        log.info("Deleting user {}", user.getUsername());
        userDao.deleteUser(user);
    }

    @Override
    public boolean existsUser(String username) {
        return userDao.existsUser(username);
    }

    @Override
    public User updateUser(User user) {
        checkExperience(user);
        log.info("Updated user {}", user.getUsername());
        return userDao.saveUser(user);
    }

    private void checkExperience(User user) {
        int experience = user.getExperience();
        if (experience > EXPERT_MIN_EXP) {
            user.setGrade(User.Grade.EXPERT);
        } else if (experience > ADVANCED_MIN_EXP) {
            user.setGrade(User.Grade.ADVANCED);
        } else if (experience > INTERMEDIATE_MIN_EXP) {
            user.setGrade(User.Grade.INTERMEDIATE);
        } else {
            user.setGrade(User.Grade.BEGINNER);
        }
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (username == null)
            return null;
        return userDao.getUserByUsername(username);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, 1); // Add one day to get the start of tomorrow
        return new Date(calendar.getTimeInMillis());
    }

    @Override
    public void checkIn(User user) {
        Date lastCheckin = user.getLast_checkin();
        Date tomorrow = getTomorrow();
        if (lastCheckin == null || lastCheckin.before(tomorrow)) {
            log.info("User {} checked in today", user.getUsername());
            user.setExperience(user.getExperience() + 1);
            user.setLast_checkin(tomorrow);
            updateUser(user);
        }
    }

    @Override
    public boolean hasCheckedInToday(User user) {
        Date lastCheckin = user.getLast_checkin();
        Date tomorrow = getTomorrow();
        return lastCheckin != null && !lastCheckin.before(tomorrow);
    }

    @Override
    public void sendVerificationCode(User user, String email) {
        emailVerifier.send(user, email);
    }

    @Override
    public boolean verifyEmail(User user, String email, String code) {
        return emailVerifier.verify(user, email, code);
    }

    @Override
    public int saveUserAvatar(User user, byte[] avatar) {
        // save the avatar to the target folder, in order to make it accessible by the web
        String folder = Objects.requireNonNull(getClass().getClassLoader()
                .getResource("static/img/user")).getPath();
        int a1 = saveUserAvatarToPath(user, avatar, folder);

        // save the avatar to local folder, in order to make it accessible by the server
        folder = "/src/main/resources/static/img/user";
        int a2 = saveUserAvatarToPath(user, avatar, folder);

        return a1 == 0 && a2 == 0 ? 0 : -1;
    }

    private int saveUserAvatarToPath(User user, byte[] avatar, String usersFolder) {

        // make the directory first
        String userFolder = usersFolder + "/" + user.getName();
        if (userFolder.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("windows")) {
            userFolder = userFolder.substring(1);
        }
        try {
            Files.createDirectories(Paths.get(userFolder));
        } catch (IOException ex) {
            log.info("Error when making the directory for user {}", user.getName());
        }
        Path avatarPath = Paths.get(userFolder + "/avatar");

        if (avatar == null) {
            // delete the avatar
            try {
                Files.deleteIfExists(avatarPath);
                log.info("User {}cleared avatar.", user.getName());
            } catch (IOException e) {
                log.warn("Error while deleting avatar: {}", e.getMessage());
                return -1;
            }
            return 0;
        }

        if (avatar.length > 16 * 1024 * 1024) // 16MB
            return -1;

        try {
            // save the avatar
            Files.write(avatarPath, avatar);
            log.info("Saving avatar for user {}", user.getName());
        } catch (IOException e) {
            log.warn("Error while saving avatar: {}", e.getMessage());
            return -1;
        }
        return 0;
    }
}