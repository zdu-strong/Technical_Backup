package com.springboot.project.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class StorageSpaceEntity {
	@Id
	private String id;

	@Column(nullable = false)
	private String folderName;

	@Column(nullable = false)
	private Date createDate;

	@Column(nullable = false)
	private Date updateDate;
}
