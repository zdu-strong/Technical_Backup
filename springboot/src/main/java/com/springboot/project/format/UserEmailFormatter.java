package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserEmailEntity;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.service.BaseService;

@Service
public class UserEmailFormatter extends BaseService {

	public UserEmailModel format(UserEmailEntity userEmailEntity) {
		var userEmailModel = new UserEmailModel().setId(userEmailEntity.getId()).setEmail(userEmailEntity.getEmail())
				.setUser(this.userFormatter.format(userEmailEntity.getUser()));
		return userEmailModel;
	}
}
