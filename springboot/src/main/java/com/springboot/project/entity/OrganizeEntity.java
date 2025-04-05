package com.springboot.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "parentId", "name", "deactivateKey" })
}, indexes = {
        @Index(columnList = "parentId, isActive"),
        @Index(columnList = "createDate, id"),
        @Index(columnList = "isCompany, isActive")
})
public class OrganizeEntity {

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
    private Boolean isCompany;

    @Column(nullable = false)
    private String deactivateKey;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = true)
    private OrganizeEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeEntity> childList;

    @OneToMany(mappedBy = "ancestor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeRelationEntity> descendantList;

    @OneToMany(mappedBy = "descendant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrganizeRelationEntity> ancestorList;

    @OneToMany(mappedBy = "organize", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RoleOrganizeRelationEntity> roleOrganizeRelationList;

}
