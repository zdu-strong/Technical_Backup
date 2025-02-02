package com.springboot.project.test.controller.UserRoleController;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.RoleModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserRoleControllerCreateTest extends BaseTest {

    private UserModel user;

    @Test
    public void test() throws URISyntaxException {
        var body = new RoleModel();
        body.setName("Manager");
        body.setOrganizeList(JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getOrganizeList())
                .group(s -> s.getId(), (s, t) -> t.findFirst().get())
                .select(s -> s.getTwo())
                .toList());
        body.setPermissionList(List.of(SystemPermissionEnum.ORGANIZE_MANAGE.getValue()));
        var url = new URIBuilder("/role/create").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(body), RoleModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.user = this.createAccountOfCompanyAdmin(email);
    }

}
