package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
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
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RoleService extends BaseService {

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

    @Autowired
    private RoleOrganizeRelationService roleOrganizeRelationService;

    public RoleModel create(String role, List<SystemPermissionEnum> permissionList, List<String> organizeIdList) {
        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(role);
        roleEntity.setIsActive(true);
        roleEntity.setDeactiveKey(StringUtils.EMPTY);
        roleEntity.setIsOrganizeRole(!organizeIdList.isEmpty());
        this.persist(roleEntity);

        for (var permissionEnum : permissionList) {
            this.rolePermissionRelationService.create(roleEntity.getId(), permissionEnum);
        }

        for (var organizeId : organizeIdList) {
            this.roleOrganizeRelationService.create(roleEntity.getId(), organizeId);
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
            if (roleModel.getPermissionList().contains(rolePermissionRelationEntity.getPermission().getName())) {
                continue;
            }
            this.remove(rolePermissionRelationEntity);
        }

        for (var permissionEnum : roleModel.getPermissionList().stream()
                .map(s -> SystemPermissionEnum.parse(s))
                .toList()) {
            if (JinqStream.from(rolePermissionRelationList)
                    .select(s -> s.getPermission().getName())
                    .toList()
                    .contains(permissionEnum.getValue())) {
                continue;
            }
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
    public PaginationModel<RoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
        var stream = this.streamAll(RoleEntity.class)
                .where(s -> !s.getIsOrganizeRole())
                .where(s -> s.getIsActive());
        return new PaginationModel<>(pageNum, pageSize, stream, this.roleFormatter::format);
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchOrganizeRoleForSuperAdminByPagination(long pageNum, long pageSize,
            String organizeId, boolean isIncludeDescendant) {
        var roleOrganizeRelationStream = this.streamAll(RoleOrganizeRelationEntity.class)
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId));
        if (!isIncludeDescendant) {
            roleOrganizeRelationStream = roleOrganizeRelationStream
                    .where(s -> s.getTwo().getDescendant().getId().equals(organizeId));
        }
        var stream = roleOrganizeRelationStream.select(s -> s.getOne().getRole());
        return new PaginationModel<>(pageNum, pageSize, stream, this.roleFormatter::format);
    }

    @Transactional(readOnly = true)
    public void checkCannotBeEmptyOfName(RoleModel roleModel) {
        if (StringUtils.isBlank(roleModel.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System role name cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public void checkCannotBeEmptyOfPermissionList(RoleModel roleModel) {
        if (CollectionUtils.isEmpty(roleModel.getPermissionList())) {
            roleModel.setPermissionList(List.of());
        }
        if (CollectionUtils.isEmpty(roleModel.getOrganizeList())) {
            roleModel.setOrganizeList(List.of());
        }
        if (roleModel.getPermissionList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission list cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public void checkCanCreateUserRole(RoleModel roleModel, HttpServletRequest request) {
        if (!roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> !m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            return;
        }
        if (roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot have organization permissions");
        }
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);
    }

    @Transactional(readOnly = true)
    public void checkCanCreateOrganizeRole(RoleModel roleModel, HttpServletRequest request) {
        if (!roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            return;
        }
        if (roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> !m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot have system permissions");
        }
        if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
            return;
        }
        for (var permissionName : roleModel.getPermissionList()) {
            for (var organizeModel : roleModel.getOrganizeList()) {
                this.permissionUtil.checkAnyPermission(request, organizeModel.getId(),
                        SystemPermissionEnum.parse(permissionName));
            }
        }
    }

    public void refreshForCompany(String companyId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }

            var roleName = systemRoleEnum.getValue();
            var exist = this.streamAll(RoleOrganizeRelationEntity.class)
                    .where(s -> s.getOrganize().getId().equals(companyId))
                    .where(s -> s.getRole().getName().equals(roleName))
                    .exists();

            if (exist) {
                continue;
            }
            this.create(roleName, systemRoleEnum.getPermissionList(), List.of(companyId));
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

    private boolean deleteUserRoleList() {
        return false;
    }

    private boolean createUserRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var roleName = systemRoleEnum.getValue();
            if (this.streamAll(RoleEntity.class)
                    .where(s -> !s.getIsOrganizeRole())
                    .where(s -> s.getName().equals(roleName))
                    .exists()) {
                continue;
            }
            this.create(roleName,
                    systemRoleEnum.getPermissionList(),
                    List.of());
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
            var roleName = systemRoleEnum.getValue();
            var organizeId = this.streamAll(OrganizeEntity.class)
                    .where(s -> s.getIsCompany())
                    .where(s -> s.getIsActive())
                    .leftOuterJoinList(s -> s.getRoleOrganizeRelationList())
                    .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getRole()),
                            (s, t) -> t.getName().equals(roleName))
                    .select((s, t) -> new Pair<>(s.getOne().getOne().getId(), s.getTwo() == null ? 0 : 1))
                    .group((s) -> s.getOne(), (s, t) -> t.sumInteger(m -> m.getTwo()))
                    .where(s -> s.getTwo() == 0)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(organizeId)) {
                this.create(roleName, systemRoleEnum.getPermissionList(), List.of(organizeId));
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
