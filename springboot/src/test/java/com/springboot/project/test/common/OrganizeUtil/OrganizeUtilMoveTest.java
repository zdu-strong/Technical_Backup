package com.springboot.project.test.common.OrganizeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeUtilMoveTest extends BaseTest {

    private String organizeId;
    private String parentOrganizeId;

    @Test
    public void test() {
        this.organizeUtil.move(organizeId, parentOrganizeId);
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.parentOrganizeId);
        assertEquals(1, result.getTotalRecord());
        assertEquals(organizeId, JinqStream.from(result.getList()).select(s -> s.getId()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
            var childOrganize = this.organizeService.create(childOrganizeModel);
            this.organizeId = childOrganize.getId();
            this.organizeUtil.refresh(parentOrganize.getId());
            var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(1, result.getTotalRecord());
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(0, result.getTotalRecord());
            this.parentOrganizeId = parentOrganize.getId();
        }
    }
}
