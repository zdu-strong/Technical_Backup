package com.springboot.project.controller;

import java.util.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class EncryptDecryptController extends BaseController {

    @GetMapping("/encrypt-decrypt/rsa/public-key")
    public ResponseEntity<?> getPublicKeyOfRSA() {
        var keyOfRSAPublicKey = this.encryptDecryptService.getKeyOfRSAPublicKey();
        var publicKey = Base64.getEncoder().encodeToString(keyOfRSAPublicKey.getEncoded());
        return ResponseEntity.ok(publicKey);
    }

}
