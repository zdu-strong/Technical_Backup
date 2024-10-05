package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilIsSignInTest extends BaseTest {
    @Test
    public void test() {
        var isSignIn = this.permissionUtil.isSignIn(this.request);
        assertTrue(isSignIn);
    }

    @BeforeEach
    public void beforeEach() {
        var user = this.createAccount("zdu.strong@gmail.com");
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
