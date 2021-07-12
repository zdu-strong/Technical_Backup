package com.springboot.project.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.permission.TokenUtil;
import com.springboot.project.common.storage.ResourceHttpHeadersUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;

/**
 * Base class for all controllers, providing all service variables
 * 
 * @author zdu
 *
 */
@RestController
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected Storage storage;

    @Autowired
    protected AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    protected UserService userService;

    @Autowired
    protected LongTermTaskService longTermTaskService;

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected UserMessageService userMessageService;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

}
