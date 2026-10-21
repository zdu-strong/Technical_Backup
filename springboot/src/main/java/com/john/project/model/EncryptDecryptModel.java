package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class EncryptDecryptModel {

    private String id;
    private String name;
    private String publicKeyOfMLKEM;
    private String privateKeyOfMLKEM;
    private String secretKeyOfAES;
    private Date createDate;
    private Date updateDate;

}
