package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerGetOrganizeByIdTest extends BaseTest {

    private String organizeId;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/organize").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.getForEntity(url, OrganizeModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertEquals(36, response.getBody().getId().length());
        assertEquals("Super Saiyan Son Goku", response.getBody().getName());
        assertEquals(0, response.getBody().getLevel());
        assertTrue(StringUtils.isBlank(response.getBody().getParent().getId()));
        assertEquals(0, response.getBody().getChildList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var organize = this.organizeService.create(organizeModel);
        this.organizeId = organize.getId();
    }
}
