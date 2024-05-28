package org.dindier.oicraft.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.dindier.oicraft.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Email verification utility class
 *
 * @author Rzb, LeoDreamer
 */
@Slf4j
@Component
public class EmailVerifier {
    private final Map<Pair<String, String>, VerificationCode> verificationCodes
            = new ConcurrentHashMap<>();
    private static final long VALID_TIME = TimeUnit.MINUTES.toMillis(5);
    private JavaMailSender mailSender;
    private final Timer timer = new Timer();

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

    public void send(User user, String email) {
        String username = user.getUsername();
        String verificationCode = UUID.randomUUID().toString();
        Pair<String, String> key = new Pair<>(username, email);
        verificationCodes.put(key, new VerificationCode(verificationCode));
        MimeMessage mailMessage = mailSender.createMimeMessage();
        // read email.html and replace the username and placeholder with the verification code
        URL emailUrl = getClass().getClassLoader()
                .getResource("static/html/email.html");
        if (emailUrl == null) {
            log.warn("Error while trying to send email to {}", email);
            return;
        }
        String htmlMsg;
        try (InputStream emailStream = emailUrl.openStream()) {
            htmlMsg = new String(emailStream.readAllBytes());
        } catch (IOException e) {
            log.warn("Error while trying to send email to {}", email);
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
            log.info("Error while trying to send email to {}", email);
        }
        mailSender.send(mailMessage);
        log.info("Verification code sent to {}", email);
        // Auto-delete the verification code in 5 minutes
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verificationCodes.remove(key);
            }
        }, VALID_TIME);
    }

    public boolean verify(User user, String email, String code) {
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
        log.info("Email from {} verified successfully", email);
        return true;
    }
}
