package com.springboot.project.test.service.OrganizeCheckService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeCheckServiceCheckExistOrganizeNotExistOrganizeTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.organizeCheckService.checkExistOrganize(Generators.timeBasedReorderedGenerator().generate().toString());
        });
    }

}
