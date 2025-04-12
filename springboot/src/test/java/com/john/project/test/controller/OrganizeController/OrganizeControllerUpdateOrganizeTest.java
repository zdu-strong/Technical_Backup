package com.john.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerUpdateOrganizeTest extends BaseTest {

    private OrganizeModel organizeModel;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/organize/update").build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(organizeModel),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var result = this.organizeService.getById(organizeModel.getId());
        assertNotNull(result.getId());
        assertEquals(36, result.getId().length());
        assertEquals("Piccolo", result.getName());
        assertEquals(0, result.getLevel());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(0, result.getChildCount());
        assertEquals(0, result.getDescendantCount());
        assertTrue(StringUtils.isBlank(result.getParent().getId()));
        assertEquals(0, result.getChildList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeModel = this.organizeService.create(organizeModel);
        this.organizeModel.setName("Piccolo");
    }
}
