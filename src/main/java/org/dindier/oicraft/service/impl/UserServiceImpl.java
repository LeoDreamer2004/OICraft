package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.assets.constant.ConfigConstants;
import org.dindier.oicraft.assets.exception.*;
import org.dindier.oicraft.dao.UserDao;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.dindier.oicraft.util.code.lang.Platform;
import org.dindier.oicraft.util.email.EmailVerifier;
import org.dindier.oicraft.util.web.ImageUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Calendar;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private EmailVerifier emailVerifier;

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
    @NotNull
    public User getUserById(int id) throws EntityNotFoundException {
        User user = userDao.getUserById(id);
        if (user == null)
            throw new EntityNotFoundException(User.class);
        return user;
    }

    @Override
    public User getUserByUsername(String name) {
        return userDao.getUserByUsername(name);
    }

    @Override
    public boolean isLegalUsername(String username) {
        return ConfigConstants.USER_NAME_PATTERN.matcher(username).matches();
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
        userDao.deleteUser(user.getId());
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
        if (experience > ConfigConstants.EXPERT_MIN_EXP) {
            user.setGrade(User.Grade.EXPERT);
        } else if (experience > ConfigConstants.ADVANCED_MIN_EXP) {
            user.setGrade(User.Grade.ADVANCED);
        } else if (experience > ConfigConstants.INTERMEDIATE_MIN_EXP) {
            user.setGrade(User.Grade.INTERMEDIATE);
        } else {
            user.setGrade(User.Grade.BEGINNER);
        }
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @NotNull
    @Override
    public User getUserByRequest(HttpServletRequest request) throws UserNotLoggedInException {
        String username = request.getRemoteUser();
        if (username == null)
            throw new UserNotLoggedInException();
        User user = userDao.getUserByUsername(username);
        if (user == null)
            throw new UserNotLoggedInException();
        return user;
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
    public void sendVerificationCode(User user, String email) throws EmailVerificationError {
        emailVerifier.send(user, email);
    }

    @Override
    public boolean verifyEmail(User user, String email, String code) {
        return emailVerifier.verify(user, email, code);
    }

    @Override
    public void saveUserAvatar(User user, byte[] avatar) throws BadFileException {
        String userAvatar = user.userAvatarFilePath();
        String userOriginalAvatar = user.userOriginalAvatarFilePath();
        if (userAvatar.startsWith("/") && Platform.detect() == Platform.WINDOWS) {
            userAvatar = userAvatar.substring(1);
        }
        Path avatarPath = Paths.get(userAvatar);
        Path originalAvatarPath = Paths.get(userOriginalAvatar);

        if (avatar == null) {
            // delete the avatarStream
            try {
                Files.deleteIfExists(avatarPath);
                Files.deleteIfExists(originalAvatarPath);
                log.info("User {} cleared avatarStream.", user.getName());
            } catch (IOException e) {
                throw new BadFileException("由于服务器内部IO异常，无法删除头像");
            }
            return;
        }

        if (avatar.length > ConfigConstants.MAX_AVATAR_SIZE * 1024 * 1024)
            throw new BadFileException("头像文件过大");

        try {
            // save the avatarStream
            Files.write(avatarPath, ImageUtil.compressImage(avatar,
                    ConfigConstants.COMPRESSED_AVATAR_SIZE));
            Files.write(originalAvatarPath, avatar);
            log.info("Saving avatarStream for user {}", user.getName());
        } catch (IOException e) {
            throw new BadFileException("由于服务器内部IO异常，无法保存头像");
        }
    }

    @Override
    public void checkCanEditUserAuthentication(User operator, User user) throws AdminOperationError {
        if (!operator.isAdmin())
            throw new AdminOperationError("权限不足");
        if (operator.equals(user))
            throw new AdminOperationError("不可以修改自己的权限");
        if (user.getId() == 1)
            throw new AdminOperationError("不允许编辑 1 号用户");
    }

    @Getter
    public static class VerificationCode {
        private final String code;
        private final long timestamp;

        public VerificationCode(String code) {
            this.code = code;
            this.timestamp = System.currentTimeMillis();
        }
    }
}