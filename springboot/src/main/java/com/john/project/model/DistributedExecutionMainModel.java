package com.john.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionMainModel {

    private String id;

    private String executionType;

    private Long totalPage;

    private Long totalPartition;

    private String status;

    private Date createDate;

    private Date updateDate;

}
