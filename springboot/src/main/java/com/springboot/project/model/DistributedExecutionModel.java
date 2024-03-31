package com.springboot.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionModel<T> {

    private String id;
    private String name;
    private String version;
    private Boolean isLastOfExtraExecuteContent;
    private String extraExecuteContent;
    private String uniqueCodeOfExtraExecuteContent;
    private Date createDate;
    private Date updateDate;
    private Boolean isDone;
    private Boolean hasError;
    private PaginationModel<T> pagination;

}
