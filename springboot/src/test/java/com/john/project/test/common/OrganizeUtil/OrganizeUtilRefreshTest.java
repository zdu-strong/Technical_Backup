package com.john.project.test.common.OrganizeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeUtilRefreshTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeUtil.refresh(organizeId);
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(1, result.getTotalRecords());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
        this.organizeService.create(
                new OrganizeModel().setName("Son Gohan").setParent(new OrganizeModel().setId(organizeId)));
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(0, result.getTotalRecords());
    }
}
