package com.springboot.project.common.baseController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;
import com.springboot.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.springboot.project.common.utcOffsetUtil.UTCOffsetUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.FriendshipService;
import com.springboot.project.service.LongTermTaskCheckService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeCheckService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserCheckService;
import com.springboot.project.service.UserEmailCheckService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageCheckService;
import com.springboot.project.service.UserMessageService;
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
    protected UTCOffsetUtil utcOffsetUtil;

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

    protected void checkTimeZone(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
        var utcOffset = String.format("%tz", zonedDateTime);
        utcOffset = utcOffset.substring(0, 3) + ":" + utcOffset.substring(3, 5);
        if (utcOffset.length() != 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
        if (!Pattern.compile(
                "^[" + Pattern.quote("+") + Pattern.quote("-") + "]{1}" + "[0-9]{2}" + Pattern.quote(":") + "[0-9]{2}$")
                .matcher(utcOffset).find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
    }

}
