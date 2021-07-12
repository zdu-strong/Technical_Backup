package com.springboot.project.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.springboot.project.model.OrganizeModel;
import com.beust.jcommander.internal.Lists;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.entity.*;

@Service
public class OrganizeService extends BaseService {
	public Boolean isChildOrganize(String childOrganizeId, String parentOrganizeId) {
		return this.OrganizeEntity().where(organize -> organize.getId().equals(childOrganizeId))
				.where(organize -> Boolean.valueOf(true)
						.equals(MysqlFunction.isChildOrganize(organize.getId(), parentOrganizeId)))
				.findOne().isPresent();
	}

	public OrganizeModel createOrganize(OrganizeModel organizeModel) {
		var parentOrganizeId = organizeModel.getParentOrganizeId();
		var parentOrganize = this.OrganizeEntity().where(organize -> organize.getId().equals(parentOrganizeId))
				.findOne().orElse(null);
		var organize = new OrganizeEntity();
		organize.setId(UUID.randomUUID().toString());
		organize.setName(organizeModel.getName());
		organize.setParentOrganize(parentOrganize);
		organize.setChildOrganizeList(Lists.newArrayList());
		this.entityManager.persist(organize);
		return this.organizeFormatter.format(organize);
	}
}
