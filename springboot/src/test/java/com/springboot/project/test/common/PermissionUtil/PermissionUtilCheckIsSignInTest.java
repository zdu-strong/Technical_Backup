package com.springboot.project.test.common.PermissionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilCheckIsSignInTest extends BaseTest {
    @Test
    public void test() {
        this.permissionUtil.checkIsSignIn(this.request);
    }

    @BeforeEach
    public void beforeEach() {
        var user = this.createAccount("zdu.strong@gmail.com");
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
