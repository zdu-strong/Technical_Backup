package com.springboot.project.test.controller.AuthorizationController;

import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignOutNotSignInTest extends BaseTest {

    @Test
    public void test() {
        this.signOut();
    }

}
