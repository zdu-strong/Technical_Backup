package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionMainEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String executionType;

    @Column(nullable = false)
    private Long totalPage;

    @Column(nullable = false)
    private Long totalPartition;

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

    @OneToMany(mappedBy = "distributedExecutionMain", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<DistributedExecutionDetailEntity> distributedExecutionDetailList;

}
