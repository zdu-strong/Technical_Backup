package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import com.fasterxml.uuid.Generators;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerCreateOrganizeTest extends BaseTest {

    private OrganizeModel organizeModel;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/organize/create").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(organizeModel), OrganizeModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(36, response.getBody().getId().length());
        assertEquals(this.organizeModel.getParent().getId(), response.getBody().getParent().getId());
        assertEquals("Son Gohan", response.getBody().getName());
        assertEquals(0, response.getBody().getChildList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.create(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan")
                .setParent(new OrganizeModel().setId(parentOrganize.getId()));
        this.organizeModel = childOrganizeModel;
    }
}
