package com.john.project.common.baseController;

import com.john.project.common.EncryptDecryptUtil.EncryptDecryptUtil;
import com.john.project.common.FieldValidationUtil.ValidationFieldUtil;
import com.john.project.common.uuid.UUIDUtil;
import com.john.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import tools.jackson.databind.ObjectMapper;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.OrganizeUtil.OrganizeUtil;
import com.john.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.john.project.common.TimeZoneUtil.TimeZoneUtil;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.properties.AuthorizationEmailProperties;
import com.john.project.common.storage.Storage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base class for all controllers, providing all service variables
 * 
 * @author John Williams
 *
 */
@RestController
public abstract class BaseController {

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
    protected UUIDUtil uuidUtil;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected UserService userService;

    @Autowired
    protected LongTermTaskService longTermTaskService;

    @Autowired
    protected EncryptDecryptUtil encryptDecryptUtil;

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
    protected PermissionService permissionService;

    @Autowired
    protected PermissionRelationService permissionRelationService;

}
