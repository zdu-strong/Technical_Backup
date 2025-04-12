package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserMessageWebSocketReceiveModel {

    private Long pageNum;

    private Boolean isCancel;
}
