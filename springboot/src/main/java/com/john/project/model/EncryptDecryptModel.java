package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EncryptDecryptModel {

    private String publicKeyOfRSA;
    private String privateKeyOfRSA;

}
