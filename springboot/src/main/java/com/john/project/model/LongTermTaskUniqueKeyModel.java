package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LongTermTaskUniqueKeyModel {

    private String type;

    private String uniqueKey;

}
