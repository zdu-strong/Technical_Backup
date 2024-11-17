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
        return systemRoleModel;
    }

}
