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

    /**
     * Is it running or has ended
     */
    private Boolean isDone;

    /**
     * Whether an exception occurs
     */
    private Boolean hasError;

    private Date createDate;

    private Date updateDate;

    private DistributedExecutionMainModel distributedExecutionMain;

}
