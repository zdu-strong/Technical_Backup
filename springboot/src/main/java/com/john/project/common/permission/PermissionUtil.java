package com.john.project.common.permission;

import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.service.OrganizeService;
import com.john.project.service.TokenService;
import com.john.project.service.UserService;

@Component
public class PermissionUtil {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private UserService userService;

    public void checkIsSignIn(HttpServletRequest request) {
        if (!this.isSignIn(request)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
    }

    public boolean isSignIn(HttpServletRequest request) {
        try {
            var accessToken = this.getAccessToken(request);
            return this.tokenService.getIsValidOfAccessToken(accessToken);
        } catch (Throwable e) {
            return false;
        }
    }

    public String getUserId(HttpServletRequest request) {
        var accessToken = this.getAccessToken(request);
        var userId = JWT.decode(accessToken).getSubject();
        return userId;
    }

    public String getIdOfAccessToken(HttpServletRequest request) {
        var accessToken = this.getAccessToken(request);
        var idOfAccessToken = JWT.decode(accessToken).getId();
        return idOfAccessToken;
    }

    public boolean hasAnyPermission(HttpServletRequest request, SystemPermissionEnum... permissionList) {
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var hasAnyPermission = JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getPermissionList())
                .select(s -> s.getPermission())
                .select(s -> SystemPermissionEnum.parse(s))
                .where(s -> ArrayUtils.contains(permissionList, s))
                .exists();
        return hasAnyPermission;
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
                && JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getPermissionList())
                .select(s -> s.getPermission())
                .select(s -> SystemPermissionEnum.parse(s))
                .anyMatch(s -> s.getIsSuperAdmin())) {
            return true;
        }
        if (!Arrays.stream(permissionList)
                .anyMatch(s -> JinqStream.from(user.getRoleList())
                        .selectAllList(t -> t.getPermissionList())
                        .select(t -> t.getPermission())
                        .select(t -> SystemPermissionEnum.parse(t))
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

    public void checkAnyPermission(HttpServletRequest request, List<String> organizeIdList,
                                   SystemPermissionEnum... permissionList) {
        for (var organizeId : organizeIdList) {
            if (!this.hasAnyPermission(request, organizeId, permissionList)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
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
        var organizeIdList = JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getPermissionList())
                .where(s -> JinqStream.of(s)
                        .select(t -> t.getPermission())
                        .select(t -> SystemPermissionEnum.parse(t))
                        .where(t -> t.getIsOrganizeRole())
                        .anyMatch(t -> ArrayUtils.contains(permissionList,
                                t)))
                .select(s -> s.getOrganize())
                .select(s -> s.getId())
                .toList();
        return organizeIdList;
    }

    private String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

}
