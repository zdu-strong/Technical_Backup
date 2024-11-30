package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "distributedExecutionId", "pageNum" }) })
@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionTaskEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Long pageNum;

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

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private DistributedExecutionEntity distributedExecution;
}
