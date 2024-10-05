package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageSpaceModel {
	private String id;
	private String folderName;
	private Date createDate;
	private Date updateDate;
}
