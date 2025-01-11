package com.springboot.project.service;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.constant.NonceConstant;
import com.springboot.project.entity.NonceEntity;
import com.springboot.project.model.NonceModel;
import com.springboot.project.model.PaginationModel;

@Service
public class NonceService extends BaseService {

    public NonceModel create(String nonce) {
        var nonceEntity = new NonceEntity();
        nonceEntity.setId(newId());
        nonceEntity.setNonce(nonce);
        nonceEntity.setCreateDate(new Date());
        nonceEntity.setUpdateDate(new Date());
        this.persist(nonceEntity);

        return this.nonceFormatter.format(nonceEntity);
    }

    public PaginationModel<NonceModel> getNonceByPagination(Long pageNum, Long pageSize) {
        var expiredDate = DateUtils.addMilliseconds(new Date(),
                (int) -NonceConstant.NONCE_SURVIVAL_DURATION.toMillis());
        var stream = this.streamAll(NonceEntity.class)
                .where(s -> s.getCreateDate().before(expiredDate))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, this.nonceFormatter::format);
    }

    public void delete(String id) {
        var nonceEntity = this.streamAll(NonceEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(nonceEntity);
    }

}
