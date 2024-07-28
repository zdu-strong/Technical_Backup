package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilCheckAnyRoleForOrganizeTest extends BaseTest {

    private UserModel user;
    private String organizeId;

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.permissionUtil.checkAnyRole(this.request, this.organizeId, SystemRoleEnum.SUPER_ADMIN);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.user = this.createAccount("zdu.strong@gmail.com");
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
    }
}
