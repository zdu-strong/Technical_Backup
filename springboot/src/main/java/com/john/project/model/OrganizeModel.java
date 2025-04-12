package com.john.project.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrganizeModel {

    private String id;
    private String name;

    /**
     * init level is 0
     */
    private Long level;
    private Date createDate;
    private Date updateDate;
    private Long childCount;
    private Long descendantCount;

    private OrganizeModel parent;
    private List<OrganizeModel> childList;
}
