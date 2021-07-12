package com.springboot.project.common.permission;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PermissionUtil {

	@Autowired
	private TokenUtil tokenUtil;

	public void checkIsSignIn(HttpServletRequest request) {
		String accessToken = this.tokenUtil.getAccessToken(request);
		this.checkIsSignIn(accessToken);
	}

	public void checkIsSignIn(String accessToken) {
		try {
			this.tokenUtil.getDecodedJWTOfAccessToken(accessToken);
		} catch (Throwable e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
		}
	}

	public boolean isSignIn(HttpServletRequest request) {
		String accessToken = this.tokenUtil.getAccessToken(request);
		return this.isSignIn(accessToken);
	}

	public boolean isSignIn(String accessToken) {
		try {
			this.tokenUtil.getDecodedJWTOfAccessToken(accessToken);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public String getUserId(HttpServletRequest request) {
		String accessToken = this.tokenUtil.getAccessToken(request);
		return this.getUserId(accessToken);
	}

	public String getUserId(String accessToken) {
		this.checkIsSignIn(accessToken);
		return this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getSubject();
	}

	public String getUsername(HttpServletRequest request) {
		String accessToken = this.tokenUtil.getAccessToken(request);
		return this.getUsername(accessToken);
	}

	public String getUsername(String accessToken) {
		this.checkIsSignIn(accessToken);
		return this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getClaim("username").asString();
	}

	public String getEmail(HttpServletRequest request) {
		String accessToken = this.tokenUtil.getAccessToken(request);
		return this.getEmail(accessToken);
	}

	public String getEmail(String accessToken) {
		this.checkIsSignIn(accessToken);
		return this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getClaim("email").asString();
	}

}
