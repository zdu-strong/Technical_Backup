package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NonceModel {

    private String id;
    private String nonce;
    private Date createDate;
    private Date updateDate;

}
