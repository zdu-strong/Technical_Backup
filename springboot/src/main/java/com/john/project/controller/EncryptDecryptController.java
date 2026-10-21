package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;

@RestController
public class EncryptDecryptController extends BaseController {

    @GetMapping("/encrypt-decrypt/ml-kem/public-key")
    public ResponseEntity<?> getPublicKeyOfMLKEM() {
        var publicKeyOfMLKEM = this.encryptDecryptService.getKeyOfPublicKeyOfMLKEM();
        return ResponseEntity.ok(publicKeyOfMLKEM);
    }

}
