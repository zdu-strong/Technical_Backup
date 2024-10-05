package com.springboot.project.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserMessageWebSocketSendModel {

    private Long totalPage;

    private List<UserMessageModel> list;
}
