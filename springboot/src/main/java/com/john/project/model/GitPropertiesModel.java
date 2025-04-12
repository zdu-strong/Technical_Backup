package com.john.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GitPropertiesModel {
    private String commitId;
    private Date commitDate;
}
