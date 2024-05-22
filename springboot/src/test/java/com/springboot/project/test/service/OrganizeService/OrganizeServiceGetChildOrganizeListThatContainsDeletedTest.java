package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceGetChildOrganizeListThatContainsDeletedTest extends BaseTest {

    private String parentOrganizeId;
    private String childOrganizeId;

    @Test
    public void test() {
        var list = this.organizeService
                .getChildOrganizeListThatContainsDeleted(1L, Long.MAX_VALUE, this.parentOrganizeId)
                .getList();
        var result = JinqStream.from(list).where(s -> s.getId().equals(this.childOrganizeId)).getOnlyValue();
        assertNotNull(result.getId());
        assertEquals(this.childOrganizeId, result.getId());
        assertEquals("Son Gohan", result.getName());
        assertEquals(0, result.getChildList().size());
        assertEquals(0, result.getChildCount());
        assertNotNull(result.getParent());
        assertEquals(1, result.getLevel());
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.create(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
        var childOrganize = this.organizeService.create(childOrganizeModel);
        this.parentOrganizeId = parentOrganize.getId();
        this.childOrganizeId = childOrganize.getId();
        this.organizeService.delete(this.childOrganizeId);
    }

}
