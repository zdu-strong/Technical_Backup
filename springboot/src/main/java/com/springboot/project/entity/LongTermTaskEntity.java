package com.springboot.project.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class LongTermTaskEntity {

	@Id
	private String id;

	@Column(nullable = false)
	private Date createDate;

	/**
	 * When the update time exceeds one minute, it means that the task is
	 * interrupted.
	 */
	@Column(nullable = false)
	private Date updateDate;

	/**
	 * Stored is the json string
	 */
	@Column(nullable = true, length = 1024 * 1024 * 1024)
	private String result;

	@Column(nullable = true, length = 1024 * 1024 * 1024)
	private String errorMessage;

	/**
	 * Is it running or has ended
	 */
	@Column(nullable = false)
	private Boolean isDone;

	/**
	 * It is success or failure
	 */
	@Column(nullable = false)
	private Boolean isSuccess;
}
