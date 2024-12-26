package com.springboot.project.model;

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

    private Long totalRecord;

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

}
