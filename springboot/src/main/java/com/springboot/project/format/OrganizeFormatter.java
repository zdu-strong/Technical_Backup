package com.springboot.project.format;

import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.service.BaseService;

@Service
public class OrganizeFormatter extends BaseService {

	public OrganizeModel format(OrganizeEntity organizeEntity) {
		var organizeModel = new OrganizeModel().setId(organizeEntity.getId()).setName(organizeEntity.getName());
		if (organizeEntity.getParentOrganize() != null) {
			organizeModel.setParentOrganizeId(organizeEntity.getParentOrganize().getId());
		}
		organizeModel.setChildOrganizeList(JinqStream.from(organizeEntity.getChildOrganizeList())
				.select(s -> new OrganizeModel().setId(s.getId())).toList());
		return organizeModel;
	}
}
