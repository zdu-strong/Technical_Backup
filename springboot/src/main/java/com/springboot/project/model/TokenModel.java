package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TokenModel {

    private String id;
    private String jwtId;
    private String uniqueOneTimePasswordLogo;
    private Date createDate;
    private Date updateDate;
    private UserModel user;

}
