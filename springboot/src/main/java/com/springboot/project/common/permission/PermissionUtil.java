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
import com.springboot.project.common.enumerate.SystemRoleEnum;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.SystemRoleService;
import com.springboot.project.service.TokenService;

@Component
public class PermissionUtil {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private SystemRoleService systemRoleService;

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
        return this.tokenService.getDecodedJWTOfAccessToken(accessToken).getSubject();
    }

    public void checkAnyRole(HttpServletRequest request, SystemRoleEnum... roleList) {
        if (roleList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        var systemRoleList = systemRoleService.getSystemRoleListForCurrentUser(request);
        var exists = JinqStream.from(systemRoleList)
                .selectAllList(s -> s.getSystemDefaultRoleList())
                .select(s -> SystemRoleEnum.valueOfRole(s.getName()))
                .where(s -> ArrayUtils.contains(roleList, s))
                .exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public void checkAnyRole(HttpServletRequest request, String organizeId, List<SystemRoleEnum> roleList) {
        if (StringUtils.isBlank(organizeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        if (roleList.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        if (roleList.stream().anyMatch(s -> !s.getIsOrganizeRole() && !s.getIsSuperAdmin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        if (roleList.stream().anyMatch(s -> s.getIsSuperAdmin())) {
            return;
        }
        var organizeIdList = getOrganizeIdListByAnyRole(request, roleList.toArray(new SystemRoleEnum[] {}));
        var ancestorIdList = this.organizeService.getAccestorIdList(organizeId);
        if (!JinqStream.from(ancestorIdList).anyMatch(s -> organizeIdList.contains(s))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public List<String> getOrganizeIdListByAnyRole(HttpServletRequest request, SystemRoleEnum... roleList) {
        if (roleList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        if (Arrays.stream(roleList).anyMatch(s -> !s.getIsOrganizeRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        return this.systemRoleService.getOrganizeIdListByAnyRole(request,
                Arrays.stream(roleList).map(s -> s.getRole()).toList());
    }

}
