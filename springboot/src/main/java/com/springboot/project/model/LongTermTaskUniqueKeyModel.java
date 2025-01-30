package com.springboot.project.model;

import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LongTermTaskUniqueKeyModel {

    private LongTermTaskTypeEnum type;

    private String uniqueKey;

}
