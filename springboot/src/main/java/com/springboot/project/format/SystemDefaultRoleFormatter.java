package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.SystemDefaultRoleEntity;
import com.springboot.project.model.SystemDefaultRoleModel;

@Service
public class SystemDefaultRoleFormatter extends BaseService {

    public SystemDefaultRoleModel format(SystemDefaultRoleEntity systemDefaultRoleEntity) {
        var systemDefaultRoleModel = new SystemDefaultRoleModel();
        BeanUtils.copyProperties(systemDefaultRoleEntity, systemDefaultRoleModel);
        return systemDefaultRoleModel;
    }

}
