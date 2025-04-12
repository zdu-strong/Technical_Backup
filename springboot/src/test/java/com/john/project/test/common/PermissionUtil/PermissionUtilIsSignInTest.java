package com.john.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class PermissionUtilIsSignInTest extends BaseTest {
    @Test
    public void test() {
        var isSignIn = this.permissionUtil.isSignIn(this.request);
        assertTrue(isSignIn);
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        var user = this.createAccount(email);
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
