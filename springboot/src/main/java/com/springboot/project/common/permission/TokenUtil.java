package com.springboot.project.common.permission;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.UserService;

import cn.hutool.core.lang.UUID;

@Component
public class TokenUtil {

	@Autowired
	private EncryptDecryptService encryptDecryptService;

	@Autowired
	private UserService userService;

	public TokenModel generateToken(String email) {
		/**
		 * get user info
		 */
		var userModel = this.userService.createUserIfNotExist(email);

		/**
		 * generate access token
		 */
		var calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, 3600);
		var expires = calendar.getTime();
		String accessToken = JWT.create().withSubject(userModel.getId())
				.withClaim("username", userModel.getUsername())
				.withClaim("email", userModel.getEmail()).withExpiresAt(expires)
				.withIssuer("springboot access_token").withIssuedAt(new Date()).withJWTId(UUID.randomUUID().toString())
				.sign(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()));

		/**
		 * generate refresh token
		 */
		var calendarOfRefreshToken = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, 3600 * 24 * 30);
		var expiresOfRefreshToken = calendarOfRefreshToken.getTime();
		String refreshToken = JWT.create().withSubject(userModel.getId())
				.withClaim("username", userModel.getUsername())
				.withClaim("email", userModel.getEmail()).withExpiresAt(expiresOfRefreshToken)
				.withIssuer("springboot refresh_token").withIssuedAt(new Date()).withJWTId(UUID.randomUUID().toString())
				.sign(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()));

		/**
		 * return token model
		 */
		var tokenModel = new TokenModel().setExpires_in(3600).setToken_type("Bearer").setAccess_token(accessToken)
				.setRefresh_token(refreshToken);
		return tokenModel;
	}

	public TokenModel refreshToken(String refreshToken) {
		/**
		 * get user info from refresh token
		 */
		var decodedJWT = this.getDecodedJWTOfRefreshToken(refreshToken);
		var userId = decodedJWT.getSubject();
		var username = decodedJWT.getClaim("username").asString();
		var userEmail = decodedJWT.getClaim("email").asString();

		/**
		 * generate access token
		 */
		var calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, 3600);
		var expires = calendar.getTime();
		String accessToken = JWT.create().withSubject(userId).withClaim("username", username)
				.withClaim("email", userEmail).withExpiresAt(expires)
				.sign(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()));
		/**
		 * generate refresh token
		 */
		var calendarOfRefreshToken = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, 3600 * 24 * 30);
		var expiresOfRefreshToken = calendarOfRefreshToken.getTime();
		String refreshTokenOfNew = JWT.create().withSubject(userId).withClaim("username", username)
				.withClaim("email", userEmail).withExpiresAt(expiresOfRefreshToken)
				.sign(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()));

		/**
		 * return token model
		 */
		var tokenModel = new TokenModel().setExpires_in(3600).setToken_type("Bearer").setAccess_token(accessToken)
				.setRefresh_token(refreshTokenOfNew);
		return tokenModel;
	}

	public String getAccessToken(HttpServletRequest request) {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!Strings.isNullOrEmpty(authorization)) {
			String prefix = "Bearer ";
			if (authorization.startsWith(prefix)) {
				return authorization.substring(prefix.length());
			}
		}
		return "";
	}

	public DecodedJWT getDecodedJWTOfAccessToken(String accessToken) {
		var decodedJWT = JWT
				.require(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()))
				.withIssuer("springboot access_token").build().verify(accessToken);
		return decodedJWT;
	}

	public DecodedJWT getDecodedJWTOfRefreshToken(String refreshToken) {
		var decodedJWT = JWT
				.require(Algorithm.RSA512(this.encryptDecryptService.getPublicKeyOfRSA(),
						this.encryptDecryptService.getPrivateKeyOfRSA()))
				.withIssuer("springboot refresh_token").build().verify(refreshToken);
		return decodedJWT;
	}

}
