package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.PermissionEntity;
import com.springboot.project.model.PermissionModel;

@Service
public class PermissionFormatter extends BaseService {

    public PermissionModel format(PermissionEntity systemRoleEntity) {
        var systemRoleModel = new PermissionModel();
        BeanUtils.copyProperties(systemRoleEntity, systemRoleModel);
        return systemRoleModel;
    }

}
