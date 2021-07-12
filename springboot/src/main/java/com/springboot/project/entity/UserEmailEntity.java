package com.springboot.project.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class UserEmailEntity {
	@Id
	private String id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private Date createDate;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
	private UserEntity user;

	public UserEmailEntity setUser(UserEntity user) {
		if (this.user != null) {
			this.user.getUserEmailList().remove(this);
		}
		this.user = user;
		if (this.user != null) {
			this.user.getUserEmailList().add(this);
		}
		return this;
	}

}
