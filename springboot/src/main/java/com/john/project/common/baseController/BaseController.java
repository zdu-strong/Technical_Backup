package com.john.project.common.baseController;

import com.john.project.common.FieldValidationUtil.ValidationFieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.OrganizeUtil.OrganizeUtil;
import com.john.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.john.project.common.TimeZoneUtil.TimeZoneUtil;
import com.john.project.common.longtermtask.LongTermTaskUtil;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.properties.AuthorizationEmailProperties;
import com.john.project.properties.DateFormatProperties;
import com.john.project.common.storage.Storage;
import com.john.project.service.EncryptDecryptService;
import com.john.project.service.FriendshipService;
import com.john.project.service.LongTermTaskService;
import com.john.project.service.OrganizeService;
import com.john.project.service.PermissionService;
import com.john.project.service.RoleOrganizeRelationService;
import com.john.project.service.RoleService;
import com.john.project.service.TokenService;
import com.john.project.service.UserEmailService;
import com.john.project.service.UserMessageService;
import com.john.project.service.UserRoleRelationService;
import com.john.project.service.UserService;
import com.john.project.service.VerificationCodeEmailService;
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
    protected ValidationFieldUtil validationFieldUtil;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

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
    protected UserEmailService userEmailService;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected FriendshipService friendshipService;

    @Autowired
    protected VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected UserRoleRelationService userRoleRelationService;

    @Autowired
    protected RoleOrganizeRelationService roleOrganizeRelationService;

    @Autowired
    protected PermissionService permissionService;

}
