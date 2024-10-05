package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.SystemRoleEntity;
import com.springboot.project.model.SystemRoleModel;

@Service
public class SystemRoleFormatter extends BaseService {

    public SystemRoleModel format(SystemRoleEntity systemRoleEntity) {
        var systemRoleModel = new SystemRoleModel();
        BeanUtils.copyProperties(systemRoleEntity, systemRoleModel);
        if (systemRoleEntity.getOrganize() != null) {
            systemRoleModel.setOrganize(this.organizeFormatter.format(systemRoleEntity.getOrganize()));
        }
        var id = systemRoleEntity.getId();

        var systemDefaultRoleList = this.SystemRoleEntity()
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemDefaultRole())
                .map(s -> this.systemDefaultRoleFormatter.format(s))
                .toList();
        systemRoleModel.setSystemDefaultRoleList(systemDefaultRoleList);
        return systemRoleModel;
    }

}
