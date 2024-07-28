package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.SystemRoleModel;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SystemRoleService extends BaseService {

    public List<SystemRoleModel> getSystemRoleListForCurrentUser(HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var systemRoleList = this.UserRoleRelationEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .select(s -> s.getSystemRole())
                .where(s -> s.getIsActive())
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        return systemRoleList;
    }

    public List<String> getOrganizeIdListByAnyRole(HttpServletRequest request, List<String> roleList) {
        var userId = this.permissionUtil.getUserId(request);
        var organizeList = JinqStream.from(this.UserRoleRelationEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .select(s -> s.getSystemRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() != null)
                .where(s -> s.getOrganize().getIsActive())
                .joinList(s -> s.getSystemRoleRelationList())
                .where(s -> roleList.contains(s.getTwo().getSystemDefaultRole().getName()))
                .select(s -> s.getOne().getOrganize())
                .map(s -> this.organizeFormatter.format(s))
                .toList())
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getLevel())
                .toList();
        var organizeIdList = new ArrayList<String>();
        for (var organize : organizeList) {
            if (organizeIdList.contains(organize.getId())) {
                continue;
            }
            if (organizeIdList.stream()
                    .anyMatch(s -> this.organizeService.isChildOfOrganize(organize.getId(), s))) {
                continue;
            }
            var organizeId = organize.getId();
            if (!this.organizeFormatter
                    .isActive(this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue())) {
                continue;
            }
            organizeIdList.add(organize.getId());
        }
        return organizeIdList;
    }

    public List<SystemRoleModel> getSystemRoleListForSuperAdmin() {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.getRole())
                .toList();
        var systemRoleList = this.SystemDefaultRoleEntity()
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        return systemRoleList;
    }

    public boolean refresh() {
        var roles = Arrays.stream(SystemRoleEnum.values()).map(s -> s.getRole()).toList();

        for (var role : roles) {
            var exists = this.SystemRoleEntity()
                    .where(s -> s.getOrganize() == null)
                    .where(s -> s.getName().equals(role))
                    .where(s -> s.getIsActive())
                    .exists();
            if (!exists) {
                var systemDefaultRoleId = this.SystemDefaultRoleEntity()
                        .where(s -> s.getName().equals(role))
                        .select(s -> s.getId())
                        .getOnlyValue();
                this.create(role, List.of(systemDefaultRoleId), null);
                return true;
            }
        }

        for (var role : roles) {
            var systemRoleEntityList = this.SystemRoleEntity()
                    .where(s -> s.getOrganize() == null)
                    .where(s -> s.getName().equals(role))
                    .where(s -> s.getIsActive())
                    .skip(1)
                    .toList();
            for (var systemRoleEntity : systemRoleEntityList) {
                systemRoleEntity.setIsActive(false);
                systemRoleEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
                systemRoleEntity.setUpdateDate(new Date());
                this.merge(systemRoleEntity);
                return true;
            }
        }

        {
            var systemRoleEntity = this.SystemRoleEntity()
                    .where(s -> !roles.contains(s.getName()))
                    .where(s -> s.getOrganize() == null)
                    .where(s -> s.getIsActive())
                    .findFirst()
                    .orElse(null);
            if (systemRoleEntity != null) {
                systemRoleEntity.setIsActive(false);
                systemRoleEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
                systemRoleEntity.setUpdateDate(new Date());
                this.merge(systemRoleEntity);
                return true;
            }
        }

        return false;
    }

    public void update(SystemRoleModel systemRole) {
        var id = systemRole.getId();
        var systemRoleEntity = this.SystemRoleEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        systemRoleEntity.setName(systemRole.getName());
        systemRoleEntity.setUpdateDate(new Date());
        this.merge(systemRoleEntity);

        var systemRoleRelationEntityList = this.SystemRoleEntity().where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getSystemRoleRelationList()).toList();
        for (var systemRoleRelationEntity : systemRoleRelationEntityList) {
            this.remove(systemRoleRelationEntity);
        }
        for (var systemDefaultRoleId : systemRole.getSystemDefaultRoleList().stream().map(s -> s.getId()).toList()) {
            this.systemRoleRelationService.create(id, systemDefaultRoleId);
        }
    }

    public void create(String role, List<String> systemDefaultRoleIdList, String organizeId) {
        OrganizeEntity organizeEntity = StringUtils.isNotBlank(organizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue()
                : null;

        var systemRoleEntity = new SystemRoleEntity();
        systemRoleEntity.setId(newId());
        systemRoleEntity.setCreateDate(new Date());
        systemRoleEntity.setUpdateDate(new Date());
        systemRoleEntity.setName(role);
        systemRoleEntity.setIsActive(true);
        systemRoleEntity.setDeactiveKey("");
        systemRoleEntity.setOrganize(organizeEntity);
        this.persist(systemRoleEntity);

        for (var systemDefaultRoleId : systemDefaultRoleIdList) {
            this.systemRoleRelationService.create(systemRoleEntity.getId(), systemDefaultRoleId);
        }
    }

}
