package com.springboot.project.test.controller.UserRoleController;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.RoleModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserRoleControllerCreateTest extends BaseTest {

    private UserModel user;

    @Test
    public void test() throws URISyntaxException {
        var body = new RoleModel();
        body.setName("Manager");
        body.setOrganize(new OrganizeModel().setId(
                user.getOrganizeRoleRelationList().stream().map(s -> s.getOrganize().getId()).findFirst().get()));
        body.setPermissionList(this.roleService.getOrganizeRoleListByCompanyId(body.getOrganize().getId()).stream()
                .map(s -> s.getPermissionList()).findFirst().get());
        var url = new URIBuilder("/user_role/create").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(body), RoleModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.user = this.createAccountOfCompanyAdmin(email);
    }

}
