package com.springboot.project.test.service.OrganizeCheckService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeCheckServiceCheckExistOrganizeTest extends BaseTest {
    private String organizeId;

    @Test
    public void test() {
        this.organizeCheckService.checkExistOrganize(this.organizeId);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
    }

}
