package com.springboot.project.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class OrganizeEntity {

	@Id
	private String id;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "parentOrganize", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrganizeEntity> childOrganizeList;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = true)
	private OrganizeEntity parentOrganize;

	public OrganizeEntity setParentOrganize(OrganizeEntity parentOrganize) {
		if (this.parentOrganize != null) {
			this.parentOrganize.getChildOrganizeList().remove(this);
		}
		this.parentOrganize = parentOrganize;
		if (this.parentOrganize != null) {
			this.parentOrganize.getChildOrganizeList().add(this);
		}
		return this;
	}
}
