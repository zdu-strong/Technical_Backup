package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilGetOrganizeIdListByAnyRoleTest extends BaseTest {
    private UserModel user;

    @Test
    public void test() {
        var result = this.permissionUtil.getOrganizeIdListByAnyRole(this.request, SystemRoleEnum.ORGANIZE_ADMIN);
        assertEquals(0, result.size());
    }

    @BeforeEach
    public void beforeEach() {
        this.user = this.createAccount("zdu.strong@gmail.com");
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
