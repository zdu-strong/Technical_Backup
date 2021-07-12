package com.springboot.project.common.permission;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.properties.AuthorizationEmailProperties;

@Component
public class AuthorizationEmailUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private AuthorizationEmailProperties authorizationEmailProperties;

	public void sendVerificationCode(String email) {
		this.sendEmail(email, "4453");
	}

	public void checkVerificationCode(String email, String verificationCode) {
		if (!"".equals("")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect verification code");
		}
	}

	private void sendEmail(String email, String verificationCode) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(this.authorizationEmailProperties.getSenderEmail());
			helper.setTo(email);
			helper.setSubject("登录的验证码");
			helper.setText(this.getEmailOfBody(verificationCode), true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String getEmailOfBody(String verificationCode) {
		try (InputStream input = ClassLoader.getSystemResourceAsStream("email/email.html")) {
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
