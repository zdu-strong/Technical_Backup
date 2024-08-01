package com.springboot.project.common.EmailUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.util.regex.Pattern;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.properties.IsDevelopmentMockModeProperties;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.service.VerificationCodeEmailService;

@Component
public class AuthorizationEmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    private IsDevelopmentMockModeProperties isDevelopmentMockModeProperties;

    @Autowired
    private VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    private DateFormatProperties dateFormatProperties;

    public VerificationCodeEmailModel sendVerificationCode(String email) throws ParseException {
        VerificationCodeEmailModel verificationCodeEmailModel = null;
        for (var i = 10; i > 0; i--) {
            var verificationCodeEmailModelTwo = this.verificationCodeEmailService.createVerificationCodeEmail(email);

            var fastDateFormat = FastDateFormat.getInstance(dateFormatProperties.getYearMonthDayHourMinuteSecond());
            var createDate = fastDateFormat.parse(
                    fastDateFormat.format(DateUtils.addSeconds(verificationCodeEmailModelTwo.getCreateDate(), 1)));
            ThreadUtils.sleepQuietly(
                    Duration.ofMillis(createDate.getTime() - verificationCodeEmailModelTwo.getCreateDate().getTime()));

            if (this.verificationCodeEmailService
                    .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailModelTwo.getId())) {
                verificationCodeEmailModel = verificationCodeEmailModelTwo;
                break;
            }
        }

        if (verificationCodeEmailModel == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many verification code requests in a short period of time");
        }

        if (!this.isDevelopmentMockModeProperties.getIsDevelopmentMockMode()) {
            this.sendEmail(email, verificationCodeEmailModel.getVerificationCode());
            verificationCodeEmailModel.setVerificationCode("");
        }

        return verificationCodeEmailModel;
    }

    private void sendEmail(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(this.authorizationEmailProperties.getSenderEmail());
            helper.setTo(email);
            helper.setSubject("Verification Code For Login");
            helper.setText(this.getEmailOfBody(verificationCode), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getEmailOfBody(String verificationCode) {
        try (InputStream input = new ClassPathResource("email/email.xml").getInputStream()) {
            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            String content = text.replaceAll(this.getRegex("verificationCode"), verificationCode);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getRegex(String name) {
        String regex = Pattern.quote("${") + "\\s*" + Pattern.quote(name) + "\\s*" + Pattern.quote("}");
        return regex;
    }

}
