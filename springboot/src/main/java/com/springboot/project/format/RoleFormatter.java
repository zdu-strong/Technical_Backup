package com.springboot.project.format;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.RoleEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.RoleModel;

@Service
public class RoleFormatter extends BaseService {

    public RoleModel format(RoleEntity userRoleEntity) {
        var roleModel = new RoleModel();
        BeanUtils.copyProperties(userRoleEntity, roleModel);
        roleModel.setOrganize(Optional.ofNullable(userRoleEntity.getOrganize())
                .map(this.organizeFormatter::format)
                .orElse(new OrganizeModel().setId(StringUtils.EMPTY)));
        var id = userRoleEntity.getId();

        var systemRoleList = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getRolePermissionRelationList())
                .select(s -> s.getPermission())
                .map(s -> this.permissionFormatter.format(s))
                .toList();
        roleModel.setPermissionList(systemRoleList);
        return roleModel;
    }

}
