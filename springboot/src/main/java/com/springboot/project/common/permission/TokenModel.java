package com.springboot.project.common.permission;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TokenModel {
	private String access_token;
	private String token_type;
	private Integer expires_in;
	private String refresh_token;
}
