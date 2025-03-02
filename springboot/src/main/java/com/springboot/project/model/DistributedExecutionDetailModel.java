package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionDetailModel {

    private String id;

    private Long pageNum;

    private Long partitionNum;

    /**
     * Whether an exception occurs
     */
    private Boolean hasError;

    private Date createDate;

    private Date updateDate;

    private DistributedExecutionMainModel distributedExecutionMain;

}
