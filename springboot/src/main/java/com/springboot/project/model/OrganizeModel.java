package com.springboot.project.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrganizeModel {

	private String id;
	private String name;
	private List<OrganizeModel> childOrganizeList;

	private String parentOrganizeId;
}
