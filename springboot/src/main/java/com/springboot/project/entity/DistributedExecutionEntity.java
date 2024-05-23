package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "version", "pageNum", "pageSize", "uniqueCodeOfExtraExecuteContent" }) })
@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private Long pageNum;

    @Column(nullable = false)
    private Long pageSize;

    @Column(nullable = false)
    private Long totalPage;

    @Column(nullable = false)
    private Long totalRecord;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String extraExecuteContent;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String pagination;

    @Column(nullable = false)
    private Boolean isLastOfExtraExecuteContent;

    @Column(nullable = false)
    private String uniqueCodeOfExtraExecuteContent;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    /**
     * Is it running or has ended
     */
    @Column(nullable = false)
    private Boolean isDone;

    /**
     * Whether an exception occurs
     */
    @Column(nullable = false)
    private Boolean hasError;
}
