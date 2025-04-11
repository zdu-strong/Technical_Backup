package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class LoggerEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String message;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String loggerName;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String callerClassName;

    @Column(nullable = false)
    private String callerMethodName;

    @Column(nullable = false)
    private Long callerLineNumber;

    @Column(nullable = false)
    private String gitCommitId;

    @Column(nullable = false)
    private Date gitCommitDate;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean hasException;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String exceptionClassName;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String exceptionMessage;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String exceptionStackTrace;

}
