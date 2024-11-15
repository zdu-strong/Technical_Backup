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
import com.springboot.project.model.SystemRoleModel;

@Service
public class SystemRoleService extends BaseService {

    public SystemRoleModel create(String role, List<String> systemDefaultRoleIdList, String organizeId) {
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

        return this.systemRoleFormatter.format(systemRoleEntity);
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

    public void delete(String id) {
        var systemRoleEntity = this.SystemRoleEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        systemRoleEntity.setIsActive(false);
        systemRoleEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
        systemRoleEntity.setUpdateDate(new Date());
        this.merge(systemRoleEntity);
    }

    @Transactional(readOnly = true)
    public List<SystemRoleModel> getOrganizeRoleListByCompanyId(String companyId) {
        var ssytemRoleList = this.SystemRoleEntity()
                .where(s -> s.getOrganize().getId().equals(companyId))
                .where(s -> s.getIsActive())
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        return ssytemRoleList;
    }

    public void refreshForCompany(String companyId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var role = systemRoleEnum.getRole();
            var systemDefaultRoleId = this.SystemDefaultRoleEntity()
                    .where(s -> s.getName().equals(role))
                    .select(s -> s.getId())
                    .findOne()
                    .orElse(null);
            if (StringUtils.isBlank(systemDefaultRoleId)) {
                continue;
            }
            var systemRoleEntity = this.SystemRoleEntity()
                    .where(s -> s.getOrganize().getId().equals(companyId))
                    .selectAllList(s -> s.getSystemRoleRelationList())
                    .where(s -> s.getSystemDefaultRole().getId().equals(systemDefaultRoleId))
                    .findFirst()
                    .orElse(null);
            if (systemRoleEntity == null) {
                this.create(role, List.of(systemDefaultRoleId), companyId);
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
    public void checkCannotBeEmptyOfName(SystemRoleModel systemRoleModel) {
        if (StringUtils.isBlank(systemRoleModel.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System role name cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<SystemRoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
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
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.getRole().equals(s.getName()) && !m.getIsOrganizeRole()))
                .toList();
        var stream = JinqStream.from(systemRoleList);
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.systemRoleFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public List<SystemRoleModel> getUserRoleListForSuperAdmin() {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .filter(s -> s.getIsSuperAdmin())
                .map(s -> s.getRole())
                .toList();
        var systemRoleList = this.SystemDefaultRoleEntity()
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.getRole().equals(s.getName()) && !m.getIsOrganizeRole()))
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        return systemRoleList;
    }

    @Transactional(readOnly = true)
    public PaginationModel<SystemRoleModel> searchOrganizeRoleForSuperAdminByPagination(long pageNum, long pageSize,
            String organizeId) {
        var stream = this.SystemRoleEntity()
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId))
                .where(s -> s.getOne().getOrganize().getIsActive())
                .select(s -> s.getOne());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.systemRoleFormatter.format(s));
    }

    private boolean createUserRoleList() {
        var roleList = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.getRole())
                .toList();
        var systemDefaultRoleEntity = this.SystemDefaultRoleEntity()
                .where(s -> roleList.contains(s.getName()))
                .leftOuterJoinList(s -> s.getSystemRoleRelationList())
                .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getSystemRole()),
                        (s, t) -> t.getIsActive() && t.getOrganize() == null)
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne().getOne())
                .findFirst()
                .orElse(null);
        if (systemDefaultRoleEntity != null) {
            this.create(systemDefaultRoleEntity.getName(), List.of(systemDefaultRoleEntity.getId()), null);
            return true;
        }
        return false;
    }

    private boolean deleteOrganizeRoleList() {
        var systemRoleEntity = this.SystemRoleEntity()
                .where(s -> s.getOrganize() != null)
                .where(s -> s.getIsActive())
                .leftOuterJoinList(s -> s.getSystemRoleRelationList())
                .leftOuterJoinList(s -> s.getOne().getUserSystemRoleRelationList())
                .where(s -> s.getTwo() == null)
                .where(s -> s.getOne().getTwo() == null)
                .select(s -> s.getOne().getOne())
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            this.delete(systemRoleEntity.getId());
        }

        return false;
    }

    private boolean createOrganizeRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var role = systemRoleEnum.getRole();
            var systemDefaultRoleId = this.SystemDefaultRoleEntity()
                    .where(s -> s.getName().equals(role))
                    .select(s -> s.getId())
                    .getOnlyValue();
            var organizeId = this.OrganizeEntity()
                    .where(s -> s.getParent() == null)
                    .where(s -> s.getIsActive())
                    .leftOuterJoin(s -> JinqStream.from(s.getSystemRoleList()))
                    .leftOuterJoin(s -> JinqStream.from(s.getTwo().getSystemRoleRelationList()))
                    .leftOuterJoin((s, t) -> JinqStream.of(s.getTwo().getSystemDefaultRole()),
                            (s, t) -> t.getId().equals(systemDefaultRoleId))
                    .select((s, t) -> new Pair<>(s.getOne().getOne().getOne().getId(), s.getTwo() == null ? 0 : 1))
                    .group((s) -> s.getOne(), (s, t) -> t.sumInteger(m -> m.getTwo()))
                    .where(s -> s.getTwo() == 0)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(organizeId)) {
                this.create(role, List.of(systemDefaultRoleId), organizeId);
                return true;
            }
        }
        return false;
    }

    private boolean deleteUserRoleList() {
        {
            var systemRoleEntity = this.SystemRoleEntity()
                    .where(s -> s.getIsActive())
                    .leftOuterJoinList(s -> s.getSystemRoleRelationList())
                    .where(s -> s.getOne().getOrganize() == null)
                    .where(s -> s.getTwo() == null)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (systemRoleEntity != null) {
                this.delete(systemRoleEntity.getId());
                return true;
            }
        }
        {
            var roleList = Arrays.stream(SystemRoleEnum.values()).filter(s -> !s.getIsOrganizeRole())
                    .map(s -> s.getRole()).toList();
            var systemRoleEntity = this.SystemRoleEntity()
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .joinList(s -> s.getSystemRoleRelationList())
                    .where(s -> !roleList.contains(s.getTwo().getSystemDefaultRole().getName()))
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (systemRoleEntity != null) {
                this.delete(systemRoleEntity.getId());
                return true;
            }
        }

        {
            var role = this.SystemRoleEntity()
                    .where(s -> s.getIsActive())
                    .where(s -> s.getOrganize() == null)
                    .group(s -> s.getName(), (s, t) -> t.count())
                    .where(s -> s.getTwo() > 1)
                    .select(s -> s.getOne())
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(role)) {
                var systemRoleEntity = this.SystemRoleEntity()
                        .where(s -> s.getIsActive())
                        .where(s -> s.getOrganize() == null)
                        .where(s -> s.getName().equals(role))
                        .sortedBy(s -> s.getCreateDate())
                        .skip(1)
                        .findFirst()
                        .orElse(null);
                if (systemRoleEntity != null) {
                    this.delete(systemRoleEntity.getId());
                }
            }
        }

        return false;
    }
}
