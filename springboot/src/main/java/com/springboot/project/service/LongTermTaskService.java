package com.springboot.project.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alibaba.fastjson.JSON;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.model.LongTermTaskModel;

import cn.hutool.core.lang.UUID;

@Service
public class LongTermTaskService extends BaseService {
	public String createLongTermTask() {
		LongTermTaskEntity longTermTaskEntity = new LongTermTaskEntity();
		longTermTaskEntity.setId(UUID.randomUUID().toString());
		longTermTaskEntity.setCreateDate(new Date());
		longTermTaskEntity.setUpdateDate(new Date());
		longTermTaskEntity.setIsDone(false);
		longTermTaskEntity.setIsSuccess(false);
		longTermTaskEntity.setResult(null);
		longTermTaskEntity.setErrorMessage(null);

		this.entityManager.persist(longTermTaskEntity);
		return longTermTaskEntity.getId();
	}

	public void updateLongTermTaskToRefreshUpdateDate(String id) {
		LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
				.getOnlyValue();
		longTermTaskEntity.setUpdateDate(new Date());
		this.entityManager.merge(longTermTaskEntity);
	}

	public void updateLongTermTaskByResult(String id, Object result) {
		LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
				.getOnlyValue();
		longTermTaskEntity.setUpdateDate(new Date());
		longTermTaskEntity.setIsDone(true);
		longTermTaskEntity.setIsSuccess(true);
		longTermTaskEntity.setResult(JSON.toJSONString(result));
		this.entityManager.merge(longTermTaskEntity);
	}

	public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
		LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
				.getOnlyValue();
		longTermTaskEntity.setUpdateDate(new Date());
		longTermTaskEntity.setIsDone(true);
		longTermTaskEntity.setIsSuccess(false);
		longTermTaskEntity.setErrorMessage(JSON.toJSONString(e));
		this.entityManager.merge(longTermTaskEntity);
	}

	public ResponseEntity<LongTermTaskModel<?>> getLongTermTask(String id) {
		LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
				.getOnlyValue();
		return this.longTermTaskFormatter.format(longTermTaskEntity);

	}

	public void checkIsExistLongTermTaskById(String id) {
		var isExistLongTermTask = this.LongTermTaskEntity().where(s -> s.getId().equals(id)).findFirst().isPresent();
		if (!isExistLongTermTask) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
		}
	}

}
