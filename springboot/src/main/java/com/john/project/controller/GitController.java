package com.john.project.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.john.project.common.baseController.BaseController;
import com.john.project.model.GitPropertiesModel;

@RestController
public class GitController extends BaseController {

    @GetMapping("/git")
    public ResponseEntity<?> getGitInfo() {
        var gitPropertiesModel = new GitPropertiesModel().setCommitId(gitProperties.getCommitId())
                .setCommitDate(Date.from(gitProperties.getCommitTime()));
        return ResponseEntity.ok(gitPropertiesModel);
    }

}
