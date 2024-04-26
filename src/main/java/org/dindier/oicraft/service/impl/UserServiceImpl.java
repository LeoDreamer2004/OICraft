package org.dindier.oicraft.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.Pair;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.dindier.oicraft.dao.UserDao;

import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private JavaMailSender mailSender;
    private Map<Pair<String,String>, String> verificationCodes = new ConcurrentHashMap<>();

    @Autowired
    private void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    private void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (!user.isAdmin()) {
            user.setGrade(User.Grade.BEGINNER);
            user.setExperience(0);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.createUser(user);
    }

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (username == null)
            return null;
        return userDao.getUserByUsername(username);
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
            logger.info("User " + user.getUsername() + " checked in today");
            userDao.addExperience(user, 1);
            user.setLast_checkin(tomorrow);
            userDao.updateUser(user);
        }
    }

    @Override
    public boolean hasCheckedInToday(User user) {
        Date lastCheckin = user.getLast_checkin();
        Date tomorrow = getTomorrow();
        return lastCheckin != null && !lastCheckin.before(tomorrow);
    }

    @Override
    public void sendVerificationCode(HttpServletRequest request, String email) {
        // TODO: Implement this method
        String username = request.getRemoteUser();
        String verificationCode = UUID.randomUUID().toString();
        Pair<String,String> key = new Pair<>(username, email);
        verificationCodes.put(key, verificationCode);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("oicraft2024@163.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("OICraft: Your verification code");
        mailMessage.setText("Your verification code is " + verificationCode);
        mailSender.send(mailMessage);
        logger.info("Verification code sent to " + email);
    }

    @Override
    public boolean verifyEmail(HttpServletRequest request, String email, String code) {
        // TODO: Implement this method
        User user = getUserByRequest(request);
        if (user == null)
            return false;
        String username = user.getUsername();
        Pair<String,String> key = new Pair<>(username, email);
        String correctCode = verificationCodes.get(key);
        if (correctCode == null || !correctCode.equals(code))
            return false;
        verificationCodes.remove(key);
        user.setEmail(email);
        userDao.updateUser(user);
        logger.info("Binding email " + email + " for user " + user.getUsername());
        return true;
    }
}