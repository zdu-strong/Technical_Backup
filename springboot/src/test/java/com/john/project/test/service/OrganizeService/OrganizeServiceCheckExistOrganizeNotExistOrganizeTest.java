package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceCheckExistOrganizeNotExistOrganizeTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.organizeService.checkHasExistById(Generators.timeBasedReorderedGenerator().generate().toString());
        });
    }

}
