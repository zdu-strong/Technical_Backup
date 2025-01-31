package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.RoleModel;

@Service
public class RoleService extends BaseService {

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

    public RoleModel create(String role, List<SystemPermissionEnum> permissionList, String organizeId) {
        var organizeEntity = Optional.ofNullable(organizeId).filter(StringUtils::isNotBlank)
                .map(s -> this.streamAll(OrganizeEntity.class)
                        .where(m -> m.getId().equals(organizeId))
                        .getOnlyValue())
                .orElse(null);

        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(role);
        roleEntity.setIsActive(true);
        roleEntity.setDeactiveKey(StringUtils.EMPTY);
        roleEntity.setOrganize(organizeEntity);
        this.persist(roleEntity);

        for (var permissionEnum : permissionList) {
            this.rolePermissionRelationService.create(roleEntity.getId(), permissionEnum);
        }

        return this.roleFormatter.format(roleEntity);
    }

    public void update(RoleModel roleModel) {
        var id = roleModel.getId();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        roleEntity.setName(roleModel.getName());
        roleEntity.setUpdateDate(new Date());
        this.merge(roleEntity);

        var rolePermissionRelationList = this.streamAll(RolePermissionRelationEntity.class)
                .where(s -> s.getRole().getId().equals(id))
                .toList();
        for (var rolePermissionRelationEntity : rolePermissionRelationList) {
            this.remove(rolePermissionRelationEntity);
        }
        for (var permissionEnum : roleModel.getPermissionList().stream()
                .map(s -> SystemPermissionEnum.valueOf(s.getName()))
                .toList()) {
            this.rolePermissionRelationService.create(id, permissionEnum);
        }
    }

    public void delete(String id) {
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        roleEntity.setIsActive(false);
        roleEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
        roleEntity.setUpdateDate(new Date());
        this.merge(roleEntity);
    }

    @Transactional(readOnly = true)
    public List<RoleModel> getOrganizeRoleListByCompanyId(String companyId) {
        var roleList = this.streamAll(RoleEntity.class)
                .where(s -> s.getOrganize().getId().equals(companyId))
                .where(s -> s.getIsActive())
                .map(s -> this.roleFormatter.format(s))
                .toList();
        return roleList;
    }

    public void refreshForCompany(String companyId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }

            var roleName = systemRoleEnum.name();
            var exist = this.streamAll(RoleEntity.class)
                    .where(s -> s.getOrganize().getId().equals(companyId))
                    .where(s -> s.getName().equals(roleName))
                    .where(s -> s.getIsActive())
                    .exists();
            if (exist) {
                continue;
            }
            this.create(roleName, systemRoleEnum.getPermissionList(), companyId);
        }
    }

    public boolean refresh() {
        if (this.deleteUserRoleList()) {
            return true;
        }
        if (this.createUserRoleList()) {
            return true;
        }
        if (this.deleteOrganizeRoleList()) {
            return true;
        }
        if (this.createOrganizeRoleList()) {
            return true;
        }
        if (this.refreshDefaultUserRoleList()) {
            return true;
        }
        if (this.refreshDefaultOrganizeRoleList()) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public void checkCannotBeEmptyOfName(RoleModel userRoleModel) {
        if (StringUtils.isBlank(userRoleModel.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System role name cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.name())
                .toList();
        var userRoleList = this.streamAll(RoleEntity.class)
                .where(s -> roles.contains(s.getName()))
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.name().equals(s.getName()) && !m.getIsOrganizeRole()))
                .toList();
        var stream = JinqStream.from(userRoleList);
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.roleFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public List<RoleModel> getUserRoleListForSuperAdmin() {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .filter(s -> s.getPermissionList().stream().anyMatch(m -> m.getIsSuperAdmin()))
                .map(s -> s.name())
                .toList();
        var userRoleList = this.streamAll(RoleEntity.class)
                .where(s -> roles.contains(s.getName()))
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.name().equals(s.getName()) && !m.getIsOrganizeRole()))
                .map(s -> this.roleFormatter.format(s))
                .toList();
        return userRoleList;
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchOrganizeRoleForSuperAdminByPagination(long pageNum, long pageSize,
            String organizeId) {
        var stream = this.streamAll(RoleEntity.class)
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId))
                .where(s -> s.getOne().getOrganize().getIsActive())
                .select(s -> s.getOne());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.roleFormatter.format(s));
    }

    private boolean deleteUserRoleList() {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.name())
                .toList();
        var id = this.streamAll(RoleEntity.class)
                .where(s -> s.getOrganize() == null)
                .where(s -> s.getIsActive())
                .where(s -> !roles.contains(s.getName()))
                .select(s -> s.getId())
                .findFirst()
                .orElse(null);
        if (StringUtils.isNotBlank(id)) {
            this.delete(id);
            return true;
        }
        return false;
    }

    private boolean createUserRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var roleName = systemRoleEnum.name();
            if (this.streamAll(RoleEntity.class)
                    .where(s -> s.getName().equals(roleName))
                    .where(s -> s.getOrganize() == null)
                    .where(s -> s.getIsActive())
                    .exists()) {
                continue;
            }
            this.create(roleName,
                    systemRoleEnum.getPermissionList(),
                    null);
            return true;
        }
        return false;
    }

    private boolean deleteOrganizeRoleList() {
        return false;
    }

    private boolean createOrganizeRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var roleName = systemRoleEnum.name();
            var organizeId = this.streamAll(OrganizeEntity.class)
                    .where(s -> s.getParent() == null)
                    .where(s -> s.getIsActive())
                    .leftOuterJoin((s, t) -> JinqStream.from(s.getUserRoleList()),
                            (s, t) -> t.getName().equals(roleName))
                    .select((s, t) -> new Pair<>(s.getOne().getId(), s.getTwo() == null ? 0 : 1))
                    .group((s) -> s.getOne(), (s, t) -> t.sumInteger(m -> m.getTwo()))
                    .where(s -> s.getTwo() == 0)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(organizeId)) {
                this.create(roleName, systemRoleEnum.getPermissionList(), organizeId);
                return true;
            }
        }
        return false;
    }

    private boolean refreshDefaultUserRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
        }

        return false;
    }

    private boolean refreshDefaultOrganizeRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }

        }
        return false;
    }

}
