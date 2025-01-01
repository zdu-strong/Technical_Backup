package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.SystemPermissionEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.RoleModel;

@Service
public class RoleService extends BaseService {

    public RoleModel create(String role, List<SystemPermissionEnum> permissionList, String organizeId) {
        OrganizeEntity organizeEntity = StringUtils.isNotBlank(organizeId)
                ? this.streamAll(OrganizeEntity.class)
                        .where(s -> s.getId().equals(organizeId))
                        .getOnlyValue()
                : null;

        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(role);
        roleEntity.setIsActive(true);
        roleEntity.setDeactiveKey("");
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

        var rolePermissionRelationList = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getRolePermissionRelationList())
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
        for (var permissionEnum : SystemPermissionEnum.values()) {
            if (!permissionEnum.getIsOrganizeRole()) {
                continue;
            }
            var permissionName = permissionEnum.name();
            if (!this.streamAll(PermissionEntity.class)
                    .where(s -> s.getName().equals(permissionName))
                    .exists()) {
                continue;
            }
            var roleEntity = this.streamAll(RoleEntity.class)
                    .where(s -> s.getOrganize().getId().equals(companyId))
                    .selectAllList(s -> s.getRolePermissionRelationList())
                    .where(s -> s.getPermission().getName().equals(permissionName))
                    .findFirst()
                    .orElse(null);
            if (roleEntity == null) {
                this.create(permissionName, List.of(permissionEnum), companyId);
            }
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
        var roles = Arrays.stream(SystemPermissionEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.name())
                .toList();
        var userRoleList = this.streamAll(PermissionEntity.class)
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getRolePermissionRelationList())
                .select(s -> s.getRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemPermissionEnum.values())
                        .anyMatch(m -> m.name().equals(s.getName()) && !m.getIsOrganizeRole()))
                .toList();
        var stream = JinqStream.from(userRoleList);
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.roleFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public List<RoleModel> getUserRoleListForSuperAdmin() {
        var roles = Arrays.stream(SystemPermissionEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .filter(s -> s.getIsSuperAdmin())
                .map(s -> s.name())
                .toList();
        var userRoleList = this.streamAll(PermissionEntity.class)
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getRolePermissionRelationList())
                .select(s -> s.getRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemPermissionEnum.values())
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

    private boolean createUserRoleList() {
        var roleList = Arrays.stream(SystemPermissionEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.name())
                .toList();
        var systemRoleEntity = this.streamAll(PermissionEntity.class)
                .where(s -> roleList.contains(s.getName()))
                .leftOuterJoinList(s -> s.getRolePermissionRelationList())
                .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getRole()),
                        (s, t) -> t.getIsActive() && t.getOrganize() == null)
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne().getOne())
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            this.create(systemRoleEntity.getName(),
                    List.of(SystemPermissionEnum.valueOf(systemRoleEntity.getName())),
                    null);
            return true;
        }
        return false;
    }

    private boolean deleteOrganizeRoleList() {
        var userRoleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getOrganize() != null)
                .where(s -> s.getIsActive())
                .leftOuterJoinList(s -> s.getRolePermissionRelationList())
                .leftOuterJoinList(s -> s.getOne().getUserRoleRelationList())
                .where(s -> s.getTwo() == null)
                .where(s -> s.getOne().getTwo() == null)
                .select(s -> s.getOne().getOne())
                .findFirst()
                .orElse(null);
        if (userRoleEntity != null) {
            this.delete(userRoleEntity.getId());
        }

        return false;
    }

    private boolean createOrganizeRoleList() {
        for (var systemRoleEnum : SystemPermissionEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var systemRoleName = systemRoleEnum.name();
            var organizeId = this.streamAll(OrganizeEntity.class)
                    .where(s -> s.getParent() == null)
                    .where(s -> s.getIsActive())
                    .leftOuterJoin(s -> JinqStream.from(s.getUserRoleList()))
                    .leftOuterJoin(s -> JinqStream.from(s.getTwo().getRolePermissionRelationList()))
                    .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getPermission()),
                            (s, t) -> t.getName().equals(systemRoleName))
                    .select((s, t) -> new Pair<>(s.getOne().getOne().getOne().getId(), s.getTwo() == null ? 0 : 1))
                    .group((s) -> s.getOne(), (s, t) -> t.sumInteger(m -> m.getTwo()))
                    .where(s -> s.getTwo() == 0)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(organizeId)) {
                this.create(systemRoleName, List.of(systemRoleEnum), organizeId);
                return true;
            }
        }
        return false;
    }

    private boolean deleteUserRoleList() {
        {
            var userRoleEntity = this.streamAll(RoleEntity.class)
                    .where(s -> s.getIsActive())
                    .leftOuterJoinList(s -> s.getRolePermissionRelationList())
                    .where(s -> s.getOne().getOrganize() == null)
                    .where(s -> s.getTwo() == null)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (userRoleEntity != null) {
                this.delete(userRoleEntity.getId());
                return true;
            }
        }
        {
            var roleList = Arrays.stream(SystemPermissionEnum.values()).filter(s -> !s.getIsOrganizeRole())
                    .map(s -> s.name()).toList();
            var userRoleEntity = this.streamAll(RoleEntity.class)
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .joinList(s -> s.getRolePermissionRelationList())
                    .where(s -> !roleList.contains(s.getTwo().getPermission().getName()))
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (userRoleEntity != null) {
                this.delete(userRoleEntity.getId());
                return true;
            }
        }

        {
            var role = this.streamAll(RoleEntity.class)
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .group(s -> s.getName(), (s, t) -> t.count())
                    .where(s -> s.getTwo() > 1)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(role)) {
                var userRoleEntity = this.streamAll(RoleEntity.class)
                        .where(s -> s.getIsActive())
                        .where(s -> s.getOrganize() == null)
                        .where(s -> s.getName().equals(role))
                        .sortedBy(s -> s.getCreateDate())
                        .skip(1)
                        .findFirst()
                        .orElse(null);
                if (userRoleEntity != null) {
                    this.delete(userRoleEntity.getId());
                }
            }
        }

        return false;
    }
}
