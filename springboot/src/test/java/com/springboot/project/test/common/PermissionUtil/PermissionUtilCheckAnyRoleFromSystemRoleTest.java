package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilCheckAnyRoleFromSystemRoleTest extends BaseTest {
    private UserModel user;

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.permissionUtil.checkAnyPermission(this.request, SystemPermissionEnum.SUPER_ADMIN);
        });
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.user = this.createAccount(email);
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
