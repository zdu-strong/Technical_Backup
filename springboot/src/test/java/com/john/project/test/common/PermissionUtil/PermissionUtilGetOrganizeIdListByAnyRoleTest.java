package com.john.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.fasterxml.uuid.Generators;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class PermissionUtilGetOrganizeIdListByAnyRoleTest extends BaseTest {
    private UserModel user;

    @Test
    public void test() {
        var result = this.permissionUtil.getOrganizeIdListByAnyPermission(this.request, SystemPermissionEnum.ORGANIZE_MANAGE);
        assertEquals(0, result.size());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.user = this.createAccount(email);
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
