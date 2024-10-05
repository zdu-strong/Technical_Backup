package com.springboot.project.common.baseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.EmailUtil.AuthorizationEmailUtil;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;
import com.springboot.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.springboot.project.common.TimeZoneUtil.TimeZoneUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.FriendshipService;
import com.springboot.project.service.LongTermTaskCheckService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeCheckService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.SuperAdminQuerySystemRoleService;
import com.springboot.project.service.SystemRoleCheckService;
import com.springboot.project.service.SystemRoleService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserCheckService;
import com.springboot.project.service.UserEmailCheckService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageCheckService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserRoleRelationCheckService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailCheckService;
import com.springboot.project.service.VerificationCodeEmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    protected ObjectMapper objectMapper;

    @Autowired
    protected Storage storage;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

    @Autowired
    protected OrganizeUtil organizeUtil;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserCheckService userCheckService;

    @Autowired
    protected LongTermTaskService longTermTaskService;

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected UserMessageService userMessageService;

    @Autowired
    protected UserEmailService userEmailService;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected FriendshipService friendshipService;

    @Autowired
    protected VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    protected OrganizeCheckService organizeCheckService;

    @Autowired
    protected LongTermTaskCheckService longTermTaskCheckService;

    @Autowired
    protected VerificationCodeEmailCheckService verificationCodeEmailCheckService;

    @Autowired
    protected UserEmailCheckService userEmailCheckService;

    @Autowired
    protected UserMessageCheckService userMessageCheckService;

    @Autowired
    protected UserRoleRelationCheckService userRoleRelationCheckService;

    @Autowired
    protected SystemRoleService systemRoleService;

    @Autowired
    protected SystemRoleCheckService systemRoleCheckService;

    @Autowired
    protected SuperAdminQuerySystemRoleService superAdminQuerySystemRoleService;

}
