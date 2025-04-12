package com.john.project.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserMessageModel {
    private String id;

    private Date createDate;

    private Date updateDate;

    private String content;

    private UserModel user;

    private Long pageNum;

    private String url;
}
