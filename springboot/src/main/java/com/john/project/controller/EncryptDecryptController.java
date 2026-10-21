package com.john.project.controller;

import cn.hutool.core.util.HexUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;

@RestController
public class EncryptDecryptController extends BaseController {

    @GetMapping("/encrypt-decrypt/ml-kem/public-key")
    public ResponseEntity<?> getPublicKeyOfMLKEM() {
        var keyOfPublicKeyOfMLKEM = this.encryptDecryptService.getKeyOfPublicKeyOfMLKEM();
        var publicKeyOfMLKEM = HexUtil.encodeHexStr(keyOfPublicKeyOfMLKEM.getEncoded());
        return ResponseEntity.ok(publicKeyOfMLKEM);
    }

}
