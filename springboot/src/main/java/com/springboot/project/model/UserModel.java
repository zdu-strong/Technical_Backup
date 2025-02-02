package com.springboot.project.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserModel {
    private String id;
    private String username;
    private Date createDate;
    private Date updateDate;
    private String password;
    private String accessToken;
    private List<UserEmailModel> userEmailList;
    private List<RoleModel> roleList;
}
