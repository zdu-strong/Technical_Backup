package com.springboot.project.service;

import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionModel;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.StorageSpaceModel;

@Service
public class DistributedExecutionService extends BaseService {

    public DistributedExecutionModel<StorageSpaceModel> getDistributedExecutionOfStorageSpace() {
        var pageSize = 1L;
        var name = DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE.name();
        var lastDistributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getName().equals(name))
                .sortedDescendingBy(s -> s.getCreateDate())
                .sortedBy(s -> s.getPageNum())
                .sortedDescendingBy(s -> s.getVersion())
                .findFirst()
                .orElse(null);
        var type = new TypeReference<PaginationModel<StorageSpaceModel>>() {
        };
        var stream = this.StorageSpaceEntity()
                .sortedDescendingBy(s -> s.getCreateDate());

        if (lastDistributedExecutionEntity != null && !lastDistributedExecutionEntity.getIsDone()
                && !lastDistributedExecutionEntity.getUpdateDate().after(DateUtils.addMinutes(new Date(), 1))) {
            return null;
        }

        if (lastDistributedExecutionEntity != null && lastDistributedExecutionEntity.getPageNum() == 1
                && lastDistributedExecutionEntity.getIsDone()
                && !DateUtils.addHours(new Date(), -1).after(lastDistributedExecutionEntity.getUpdateDate())) {
            return null;
        }

        if (lastDistributedExecutionEntity == null || (lastDistributedExecutionEntity.getPageNum() == 1
                && lastDistributedExecutionEntity.getIsDone())) {

            var distributedExecutionModel = new DistributedExecutionModel<StorageSpaceModel>();
            distributedExecutionModel.setName(name);
            distributedExecutionModel.setVersion(this.generateNewVersion());

            var pageNum = new PaginationModel<>(1L, pageSize, stream).getTotalPage();
            if (pageNum == 0) {
                pageNum = 1L;
            }
            var paginationModel = new PaginationModel<>(pageNum, pageSize, stream,
                    (s) -> this.storageSpaceFormatter.format(s));
            distributedExecutionModel.setPagination(paginationModel);
            var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
            return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
        }

        {

            if (!lastDistributedExecutionEntity.getIsDone()) {
                var distributedExecutionModel = this.distributedExecutionFormatter
                        .format(lastDistributedExecutionEntity, type);
                this.remove(lastDistributedExecutionEntity);
                var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
                return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
            }
        }

        {
            var distributedExecutionModel = new DistributedExecutionModel<StorageSpaceModel>();
            distributedExecutionModel.setName(name);
            distributedExecutionModel.setVersion(this.generateNewVersion());
            var pagination = new PaginationModel<>(lastDistributedExecutionEntity.getPageNum() - 1, pageSize, stream,
                    s -> this.storageSpaceFormatter.format(s));
            distributedExecutionModel.setPagination(pagination);
            var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
            return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
        }

    }

    public DistributedExecutionModel<OrganizeModel> getDistributedExecutionOfOrganize() {
        var pageSize = 1L;
        var name = DistributedExecutionEnum.ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY.name();
        var lastDistributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getName().equals(name))
                .sortedDescendingBy(s -> s.getCreateDate())
                .sortedBy(s -> s.getPageNum())
                .sortedDescendingBy(s -> s.getVersion())
                .findFirst()
                .orElse(null);
        var type = new TypeReference<PaginationModel<OrganizeModel>>() {
        };
        var stream = this.OrganizeEntity()
                .sortedDescendingBy(s -> s.getCreateDate());

        if (lastDistributedExecutionEntity != null && !lastDistributedExecutionEntity.getIsDone()
                && !lastDistributedExecutionEntity.getUpdateDate().after(DateUtils.addMinutes(new Date(), 1))) {
            return null;
        }

        if (lastDistributedExecutionEntity != null && lastDistributedExecutionEntity.getPageNum() == 1
                && lastDistributedExecutionEntity.getIsDone()
                && !DateUtils.addSeconds(new Date(), -10).after(lastDistributedExecutionEntity.getUpdateDate())) {
            return null;
        }

        if (lastDistributedExecutionEntity == null || (lastDistributedExecutionEntity.getPageNum() == 1
                && lastDistributedExecutionEntity.getIsDone())) {
            var distributedExecutionModel = new DistributedExecutionModel<OrganizeModel>();
            distributedExecutionModel.setName(name);
            distributedExecutionModel.setVersion(this.generateNewVersion());

            var pageNum = new PaginationModel<>(1L, pageSize, stream).getTotalPage();
            if (pageNum == 0) {
                pageNum = 1L;
            }
            var paginationModel = new PaginationModel<>(pageNum, pageSize, stream,
                    (s) -> this.organizeFormatter.format(s));
            distributedExecutionModel.setPagination(paginationModel);
            var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
            return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
        }

        {

            if (!lastDistributedExecutionEntity.getIsDone()) {
                var distributedExecutionModel = this.distributedExecutionFormatter
                        .format(lastDistributedExecutionEntity, type);
                this.remove(lastDistributedExecutionEntity);
                var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
                return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
            }
        }

        {
            var distributedExecutionModel = new DistributedExecutionModel<OrganizeModel>();
            distributedExecutionModel.setName(name);
            distributedExecutionModel.setVersion(this.generateNewVersion());
            var pagination = new PaginationModel<>(lastDistributedExecutionEntity.getPageNum() - 1, pageSize, stream,
                    s -> this.organizeFormatter.format(s));
            distributedExecutionModel.setPagination(pagination);
            var distributedExecutionEntity = this.createDistributedExecution(distributedExecutionModel);
            return this.distributedExecutionFormatter.format(distributedExecutionEntity, type);
        }

    }

    private <T> com.springboot.project.entity.DistributedExecutionEntity createDistributedExecution(
            DistributedExecutionModel<T> distributedExecutionModel) {
        var distributedExecutionEntity = new DistributedExecutionEntity();
        distributedExecutionEntity.setId(newId());
        distributedExecutionEntity.setName(distributedExecutionModel.getName());
        distributedExecutionEntity.setVersion(distributedExecutionModel.getVersion());
        distributedExecutionEntity.setPageNum(distributedExecutionModel.getPagination().getPageNum());
        distributedExecutionEntity.setPageSize(distributedExecutionModel.getPagination().getPageSize());
        distributedExecutionEntity.setTotalPage(distributedExecutionModel.getPagination().getTotalPage());
        distributedExecutionEntity.setTotalRecord(distributedExecutionModel.getPagination().getTotalRecord());
        distributedExecutionEntity
                .setIsLastOfExtraExecuteContent(distributedExecutionModel.getIsLastOfExtraExecuteContent());
        distributedExecutionEntity.setExtraExecuteContent(distributedExecutionModel.getExtraExecuteContent());
        if (distributedExecutionModel.getIsLastOfExtraExecuteContent() == null
                || StringUtils.isBlank(distributedExecutionModel.getExtraExecuteContent())) {
            distributedExecutionEntity.setIsLastOfExtraExecuteContent(true);
            distributedExecutionEntity.setExtraExecuteContent("");
        }
        distributedExecutionEntity
                .setUniqueCodeOfExtraExecuteContent(Base64.getEncoder()
                        .encodeToString(DigestUtils.sha3_512(distributedExecutionEntity.getExtraExecuteContent())));
        try {
            distributedExecutionEntity
                    .setPagination(this.objectMapper.writeValueAsString(distributedExecutionModel.getPagination()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        distributedExecutionEntity.setIsDone(false);
        distributedExecutionEntity.setHasError(false);
        distributedExecutionEntity.setCreateDate(new Date());
        distributedExecutionEntity.setUpdateDate(new Date());
        this.persist(distributedExecutionEntity);

        return distributedExecutionEntity;
    }

    private String generateNewVersion() {
        var version = FastDateFormat
                .getInstance(dateFormatProperties.getYearMonthDayHourMinuteSecond(),
                        TimeZone.getTimeZone("UTC"))
                .format(new Date())
                + " "
                + Generators.timeBasedReorderedGenerator().generate().toString();
        return version;
    }

    public void updateDistributedExecutionTaskToRefreshUpdateDate(String id) {
        var distributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (distributedExecutionEntity.getIsDone()) {
            return;
        }
        distributedExecutionEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionEntity);
    }

    public void updateDistributedExecutionTaskByResult(String id) {
        var distributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionEntity.setUpdateDate(new Date());
        distributedExecutionEntity.setIsDone(true);
        this.merge(distributedExecutionEntity);
    }

    public void updateDistributedExecutionTaskByErrorMessage(String id) {
        var distributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionEntity.setUpdateDate(new Date());
        distributedExecutionEntity.setIsDone(true);
        distributedExecutionEntity.setHasError(true);
        this.merge(distributedExecutionEntity);
    }

}
