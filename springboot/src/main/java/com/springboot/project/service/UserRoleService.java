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
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserRoleModel;

@Service
public class UserRoleService extends BaseService {

    public UserRoleModel create(String role, List<SystemRoleEnum> systemRoleList, String organizeId) {
        OrganizeEntity organizeEntity = StringUtils.isNotBlank(organizeId)
                ? this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue()
                : null;

        var userRoleEntity = new UserRoleEntity();
        userRoleEntity.setId(newId());
        userRoleEntity.setCreateDate(new Date());
        userRoleEntity.setUpdateDate(new Date());
        userRoleEntity.setName(role);
        userRoleEntity.setIsActive(true);
        userRoleEntity.setDeactiveKey("");
        userRoleEntity.setOrganize(organizeEntity);
        this.persist(userRoleEntity);

        for (var systemRoleEnum : systemRoleList) {
            this.systemRoleRelationService.create(userRoleEntity.getId(), systemRoleEnum);
        }

        return this.userRoleFormatter.format(userRoleEntity);
    }

    public void update(UserRoleModel systemRole) {
        var id = systemRole.getId();
        var userRoleEntity = this.UserRoleEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        userRoleEntity.setName(systemRole.getName());
        userRoleEntity.setUpdateDate(new Date());
        this.merge(userRoleEntity);

        var systemRoleRelationEntityList = this.UserRoleEntity().where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getSystemRoleRelationList()).toList();
        for (var systemRoleRelationEntity : systemRoleRelationEntityList) {
            this.remove(systemRoleRelationEntity);
        }
        for (var systemRoleEnum : systemRole.getSystemRoleList().stream().map(s -> SystemRoleEnum.valueOfRole(s.getName()))
                .toList()) {
            this.systemRoleRelationService.create(id, systemRoleEnum);
        }
    }

    public void delete(String id) {
        var userRoleEntity = this.UserRoleEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        userRoleEntity.setIsActive(false);
        userRoleEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
        userRoleEntity.setUpdateDate(new Date());
        this.merge(userRoleEntity);
    }

    @Transactional(readOnly = true)
    public List<UserRoleModel> getOrganizeRoleListByCompanyId(String companyId) {
        var ssytemRoleList = this.UserRoleEntity()
                .where(s -> s.getOrganize().getId().equals(companyId))
                .where(s -> s.getIsActive())
                .map(s -> this.userRoleFormatter.format(s))
                .toList();
        return ssytemRoleList;
    }

    public void refreshForCompany(String companyId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var systemRoleName = systemRoleEnum.getRole();
            if (!this.SystemRoleEntity()
                    .where(s -> s.getName().equals(systemRoleName)).exists()) {
                continue;
            }
            var userRoleEntity = this.UserRoleEntity()
                    .where(s -> s.getOrganize().getId().equals(companyId))
                    .selectAllList(s -> s.getSystemRoleRelationList())
                    .where(s -> s.getSystemRole().getName().equals(systemRoleName))
                    .findFirst()
                    .orElse(null);
            if (userRoleEntity == null) {
                this.create(systemRoleName, List.of(systemRoleEnum), companyId);
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
    public void checkCannotBeEmptyOfName(UserRoleModel userRoleModel) {
        if (StringUtils.isBlank(userRoleModel.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System role name cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<UserRoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.getRole())
                .toList();
        var userRoleList = this.SystemRoleEntity()
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getUserRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.getRole().equals(s.getName()) && !m.getIsOrganizeRole()))
                .toList();
        var stream = JinqStream.from(userRoleList);
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.userRoleFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public List<UserRoleModel> getUserRoleListForSuperAdmin() {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .filter(s -> s.getIsSuperAdmin())
                .map(s -> s.getRole())
                .toList();
        var userRoleList = this.SystemRoleEntity()
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getUserRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.getRole().equals(s.getName()) && !m.getIsOrganizeRole()))
                .map(s -> this.userRoleFormatter.format(s))
                .toList();
        return userRoleList;
    }

    @Transactional(readOnly = true)
    public PaginationModel<UserRoleModel> searchOrganizeRoleForSuperAdminByPagination(long pageNum, long pageSize,
            String organizeId) {
        var stream = this.UserRoleEntity()
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId))
                .where(s -> s.getOne().getOrganize().getIsActive())
                .select(s -> s.getOne());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.userRoleFormatter.format(s));
    }

    private boolean createUserRoleList() {
        var roleList = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.getRole())
                .toList();
        var systemRoleEntity = this.SystemRoleEntity()
                .where(s -> roleList.contains(s.getName()))
                .leftOuterJoinList(s -> s.getSystemRoleRelationList())
                .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getUserRole()),
                        (s, t) -> t.getIsActive() && t.getOrganize() == null)
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne().getOne())
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            this.create(systemRoleEntity.getName(), List.of(SystemRoleEnum.valueOfRole(systemRoleEntity.getName())), null);
            return true;
        }
        return false;
    }

    private boolean deleteOrganizeRoleList() {
        var userRoleEntity = this.UserRoleEntity()
                .where(s -> s.getOrganize() != null)
                .where(s -> s.getIsActive())
                .leftOuterJoinList(s -> s.getSystemRoleRelationList())
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
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var systemRoleName = systemRoleEnum.getRole();
            var organizeId = this.OrganizeEntity()
                    .where(s -> s.getParent() == null)
                    .where(s -> s.getIsActive())
                    .leftOuterJoin(s -> JinqStream.from(s.getUserRoleList()))
                    .leftOuterJoin(s -> JinqStream.from(s.getTwo().getSystemRoleRelationList()))
                    .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getSystemRole()),
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
            var userRoleEntity = this.UserRoleEntity()
                    .where(s -> s.getIsActive())
                    .leftOuterJoinList(s -> s.getSystemRoleRelationList())
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
            var roleList = Arrays.stream(SystemRoleEnum.values()).filter(s -> !s.getIsOrganizeRole())
                    .map(s -> s.getRole()).toList();
            var userRoleEntity = this.UserRoleEntity()
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .joinList(s -> s.getSystemRoleRelationList())
                    .where(s -> !roleList.contains(s.getTwo().getSystemRole().getName()))
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (userRoleEntity != null) {
                this.delete(userRoleEntity.getId());
                return true;
            }
        }

        {
            var role = this.UserRoleEntity()
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .group(s -> s.getName(), (s, t) -> t.count())
                    .where(s -> s.getTwo() > 1)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(role)) {
                var userRoleEntity = this.UserRoleEntity()
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
