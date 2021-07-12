package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserModel {
	private String id;
	private String username;
	private String email;

}
