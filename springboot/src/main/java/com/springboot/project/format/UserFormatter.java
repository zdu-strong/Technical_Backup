package com.springboot.project.format;

import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;
import com.springboot.project.service.BaseService;

@Service
public class UserFormatter extends BaseService {

	public UserModel format(UserEntity userEntity) {
		var email = JinqStream.from(userEntity.getUserEmailList()).select(s -> s.getEmail()).findFirst().orElse("");
		var userModel = new UserModel().setId(userEntity.getId()).setUsername(userEntity.getUsername()).setEmail(email);
		return userModel;
	}
}
