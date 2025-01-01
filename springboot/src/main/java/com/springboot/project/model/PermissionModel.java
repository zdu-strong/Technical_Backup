package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PermissionModel {

    private String id;

    private String name;

    private Date createDate;

    private Date updateDate;

}
