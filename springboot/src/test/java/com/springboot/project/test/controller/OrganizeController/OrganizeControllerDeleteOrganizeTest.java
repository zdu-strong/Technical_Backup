package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.fasterxml.uuid.Generators;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerDeleteOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/organize/delete").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Throwable.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
