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
import com.springboot.project.enums.SystemPermissionEnum;
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

    public boolean hasAnyPermission(HttpServletRequest request, SystemPermissionEnum... permissionList) {
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var hasAnyRole = JinqStream.from(List.of(
                user.getUserRoleRelationList(),
                user.getOrganizeRoleRelationList()))
                .selectAllList(s -> s)
                .select(s -> s.getRole())
                .selectAllList(s -> s.getPermissionList())
                .select(s -> SystemPermissionEnum.parseValue(s.getName()))
                .where(s -> ArrayUtils.contains(permissionList, s))
                .exists();
        return hasAnyRole;
    }

    public boolean hasAnyPermission(HttpServletRequest request, String organizeId,
            SystemPermissionEnum... permissionList) {
        if (StringUtils.isBlank(organizeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganizeId Cannot be empty");
        }
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(permissionList).anyMatch(s -> !s.getIsOrganizeRole() && !s.getIsSuperAdmin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a superAdmin and not an organize role");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);

        if (Arrays.stream(permissionList).anyMatch(s -> s.getIsSuperAdmin())
                && JinqStream.from(user.getUserRoleRelationList())
                        .selectAllList(s -> s.getRole().getPermissionList())
                        .select(s -> SystemPermissionEnum.parseValue(s.getName()))
                        .anyMatch(s -> s.getIsSuperAdmin())) {
            return true;
        }
        if (!Arrays.stream(permissionList)
                .anyMatch(s -> JinqStream.from(user.getOrganizeRoleRelationList())
                        .selectAllList(t -> t.getRole().getPermissionList())
                        .select(t -> SystemPermissionEnum.parseValue(t.getName()))
                        .toList()
                        .contains(s))) {
            return false;
        }
        var organizeIdList = getOrganizeIdListByAnyPermission(request, permissionList);
        var ancestorIdList = this.organizeService.getAccestorIdList(organizeId);
        if (!JinqStream.from(ancestorIdList).anyMatch(s -> organizeIdList.contains(s))) {
            return false;
        }

        return true;
    }

    public void checkAnyPermission(HttpServletRequest request, SystemPermissionEnum... permissionList) {
        if (!this.hasAnyPermission(request, permissionList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public void checkAnyPermission(HttpServletRequest request, String organizeId,
            SystemPermissionEnum... permissionList) {
        if (!this.hasAnyPermission(request, organizeId, permissionList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public List<String> getOrganizeIdListByAnyPermission(HttpServletRequest request,
            SystemPermissionEnum... permissionList) {
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(permissionList).anyMatch(s -> !s.getIsOrganizeRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only organize role can be transferred");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var organizeIdList = JinqStream.from(user.getOrganizeRoleRelationList())
                .where(s -> JinqStream.from(s.getRole().getPermissionList())
                        .anyMatch(t -> ArrayUtils.contains(permissionList,
                                SystemPermissionEnum.parseValue(t.getName()))))
                .select(s -> s.getOrganize().getId())
                .toList();
        return organizeIdList;
    }

}
