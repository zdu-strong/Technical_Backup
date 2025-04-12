package com.john.project.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Since the timeout period of the connection request of the cloud server is
 * limited, this class is added to return the result of this situation.
 * 
 * @author zdu
 *
 */
@Getter
@Setter
@Accessors(chain = true)
public class LongTermTaskModel<T> {

    private String id;

    /**
     * Is it running or has ended. If it is done, then it is success.
     */
    private Boolean isDone;

    private Date createDate;

    /**
     * When the update time exceeds one minute, it means that the task is
     * interrupted.
     */
    private Date updateDate;

    /**
     * Stored is the json object or json array
     */
    private T result;

    private LongTermTaskUniqueKeyModel longTermTaskUniqueKey;

}
