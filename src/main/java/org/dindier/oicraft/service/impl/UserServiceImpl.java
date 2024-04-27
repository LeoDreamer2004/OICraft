package org.dindier.oicraft.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.antlr.v4.runtime.misc.Pair;
import org.dindier.oicraft.model.User;
import org.dindier.oicraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.dindier.oicraft.dao.UserDao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private JavaMailSender mailSender;
    private final Map<Pair<String, String>, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private static final long VALID_TIME = TimeUnit.MINUTES.toMillis(5);

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
    public void sendVerificationCode(User user, String email) {
        String username = user.getUsername();
        String verificationCode = UUID.randomUUID().toString();
        Pair<String, String> key = new Pair<>(username, email);
        verificationCodes.put(key, new VerificationCode(verificationCode));
        MimeMessage mailMessage = mailSender.createMimeMessage();
        // read email.html and replace the username and placeholder with the verification code
        URL emailUrl = getClass().getClassLoader().getResource("static/html/email.html");
        if (emailUrl == null) {
            logger.warning("Error while trying to send email to " + email);
            return;
        }
        String htmlMsg;
        try (InputStream emailStream = emailUrl.openStream()) {
            htmlMsg = new String(emailStream.readAllBytes());
        } catch (IOException e) {
            logger.warning("Error while trying to send email to " + email);
            return;
        }
        htmlMsg = htmlMsg.replace("{{username}}", username);
        htmlMsg = htmlMsg.replace("{{code}}", verificationCode);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, false, "utf-8");
            mailMessage.setContent(htmlMsg, "text/html");
            helper.setTo(email);
            helper.setSubject("Your OICraft verification code");
            helper.setFrom("oicraft2024@163.com");
        } catch (MessagingException e) {
            logger.info("Error while trying to send email to " + email);
        }
        mailSender.send(mailMessage);
        logger.info("Verification code sent to " + email);
    }

    @Override
    public boolean verifyEmail(User user, String email, String code) {
        String username = user.getUsername();
        Pair<String, String> key = new Pair<>(username, email);
        if (!verificationCodes.containsKey(key))
            return false;
        String correctCode = verificationCodes.get(key).getCode();
        if (correctCode == null || !correctCode.equals(code))
            return false;
        if (System.currentTimeMillis() - verificationCodes.get(key).getTimestamp() > VALID_TIME) {
            verificationCodes.remove(key);
            return false;
        }
        verificationCodes.remove(key);
        logger.info("Email from " + email + " verified successfully");
        return true;
    }
}