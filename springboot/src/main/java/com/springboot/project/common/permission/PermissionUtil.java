package com.springboot.project.common.permission;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserService;

@Component
public class PermissionUtil {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    public void checkIsSignIn(HttpServletRequest request) {
        if (!this.isSignIn(request)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
    }

    public boolean isSignIn(HttpServletRequest request) {
        String accessToken = this.tokenService.getAccessToken(request);
        try {
            this.tokenService.getDecodedJWTOfAccessToken(accessToken);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public String getUserId(HttpServletRequest request) {
        String accessToken = this.tokenService.getAccessToken(request);
        var decodedJWT = JWT
                .require(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()))
                .build().verify(accessToken);
        return decodedJWT.getSubject();
    }

    public boolean hasAnyRole(HttpServletRequest request, SystemRoleEnum... systemRoleList) {
        if (systemRoleList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var hasAnyRole = JinqStream.from(List.of(
                user.getUserRoleRelationList(),
                user.getOrganizeRoleRelationList()))
                .selectAllList(s -> s)
                .select(s -> s.getUserRole())
                .selectAllList(s -> s.getSystemRoleList())
                .select(s -> SystemRoleEnum.valueOf(s.getName()))
                .where(s -> ArrayUtils.contains(systemRoleList, s))
                .exists();
        return hasAnyRole;
    }

    public void checkAnyRole(HttpServletRequest request, SystemRoleEnum... systemRoleList) {
        if (!this.hasAnyRole(request, systemRoleList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public void checkAnyRole(HttpServletRequest request, String organizeId, SystemRoleEnum... systemRoleList) {
        if (StringUtils.isBlank(organizeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganizeId Cannot be empty");
        }
        if (systemRoleList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(systemRoleList).anyMatch(s -> !s.getIsOrganizeRole() && !s.getIsSuperAdmin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a superAdmin and not an organize role");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);

        if (Arrays.stream(systemRoleList).anyMatch(s -> s.getIsSuperAdmin())
                && JinqStream.from(user.getUserRoleRelationList())
                        .selectAllList(s -> s.getUserRole().getSystemRoleList())
                        .select(s -> SystemRoleEnum.valueOf(s.getName()))
                        .anyMatch(s -> s.getIsSuperAdmin())) {
            return;
        }
        if (!Arrays.stream(systemRoleList)
                .anyMatch(s -> JinqStream.from(user.getOrganizeRoleRelationList())
                        .selectAllList(t -> t.getUserRole().getSystemRoleList())
                        .select(t -> SystemRoleEnum.valueOf(t.getName()))
                        .toList()
                        .contains(s))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        var organizeIdList = getOrganizeIdListByAnyRole(request, systemRoleList);
        var ancestorIdList = this.organizeService.getAccestorIdList(organizeId);
        if (!JinqStream.from(ancestorIdList).anyMatch(s -> organizeIdList.contains(s))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public List<String> getOrganizeIdListByAnyRole(HttpServletRequest request, SystemRoleEnum... systemRoleList) {
        if (systemRoleList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(systemRoleList).anyMatch(s -> !s.getIsOrganizeRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only organize role can be transferred");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var organizeIdList = JinqStream.from(user.getOrganizeRoleRelationList())
                .where(s -> JinqStream.from(s.getUserRole().getSystemRoleList())
                        .anyMatch(t -> ArrayUtils.contains(systemRoleList,
                                SystemRoleEnum.valueOf(t.getName()))))
                .select(s -> s.getOrganize().getId())
                .toList();
        return organizeIdList;
    }

}
