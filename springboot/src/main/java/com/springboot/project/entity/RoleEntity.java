package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "organizeId", "name", "deactiveKey" }) })
public class RoleEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String deactiveKey;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = true)
    private OrganizeEntity organize;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RolePermissionRelationEntity> rolePermissionRelationList;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserRoleRelationEntity> userRoleRelationList;

}
