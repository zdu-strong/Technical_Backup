package com.springboot.project.service;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.VerificationCodeEmailEntity;
import com.springboot.project.enumerate.VerificationCodeEmailEnum;
import com.springboot.project.model.VerificationCodeEmailModel;
import cn.hutool.core.util.RandomUtil;

@Service
public class VerificationCodeEmailService extends BaseService {

    public VerificationCodeEmailModel createVerificationCodeEmail(String email) {

        var verificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;

        {
            var beforeDate = DateUtils.addMonths(new Date(), -1);

            var retryCount = this.streamAll(VerificationCodeEmailEntity.class)
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> !s.getHasUsed() || !s.getIsPassed())
                    .count();
            if (retryCount > 1000 && verificationCodeLength < VerificationCodeEmailEnum.MAX_VERIFICATION_CODE_LENGTH) {
                verificationCodeLength = VerificationCodeEmailEnum.MAX_VERIFICATION_CODE_LENGTH;
            }
        }

        {
            var beforeDate = DateUtils.addDays(new Date(), -1);

            var retryCount = this.streamAll(VerificationCodeEmailEntity.class)
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> !s.getHasUsed() || !s.getIsPassed())
                    .count();

            if (retryCount > 0
                    && verificationCodeLength < VerificationCodeEmailEnum.MODERATE_VERIFICATION_CODE_LENGTH) {
                verificationCodeLength = VerificationCodeEmailEnum.MODERATE_VERIFICATION_CODE_LENGTH;
            } else if (retryCount == 0) {
                verificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;
            }
        }

        String verificationCode = "";

        for (var i = verificationCodeLength; i > 0; i--) {
            verificationCode += RandomUtil.randomLong(10);
        }

        var verificationCodeEmailEntity = new VerificationCodeEmailEntity();
        verificationCodeEmailEntity.setId(newId());
        verificationCodeEmailEntity.setEmail(email);
        verificationCodeEmailEntity.setVerificationCode(verificationCode);
        verificationCodeEmailEntity.setHasUsed(false);
        verificationCodeEmailEntity.setIsPassed(false);
        verificationCodeEmailEntity.setCreateDate(new Date());
        verificationCodeEmailEntity.setUpdateDate(new Date());
        this.persist(verificationCodeEmailEntity);

        return this.verificationCodeEmailFormatter.format(verificationCodeEmailEntity);
    }

    @Transactional(readOnly = true)
    public boolean isFirstOnTheDurationOfVerificationCodeEmail(String id) {
        var verificationCodeEmailEntity = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        var isFirstOnTheSecond = false;

        {
            var email = verificationCodeEmailEntity.getEmail();
            var createDate = verificationCodeEmailEntity.getCreateDate();

            var utcOffset = this.timeZoneUtil.getUtcOffset("UTC");
            var beforeDate = DateUtils.addSeconds(verificationCodeEmailEntity.getCreateDate(), -1);

            isFirstOnTheSecond = this.streamAll(VerificationCodeEmailEntity.class)
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> JPQLFunction
                            .formatDateAsYearMonthDayHourMinuteSecond(s.getCreateDate(), utcOffset)
                            .equals(JPQLFunction.formatDateAsYearMonthDayHourMinuteSecond(createDate, utcOffset)))
                    .where((s, t) -> !t.stream(VerificationCodeEmailEntity.class)
                            .where(m -> m.getEmail().equals(email))
                            .where(m -> beforeDate.before(m.getCreateDate()))
                            .where(m -> JPQLFunction
                                    .formatDateAsYearMonthDayHourMinuteSecond(m.getCreateDate(), utcOffset)
                                    .equals(JPQLFunction.formatDateAsYearMonthDayHourMinuteSecond(createDate,
                                            utcOffset)))
                            .where(m -> m.getHasUsed())
                            .exists())
                    .sortedBy(s -> s.getId())
                    .sortedBy(s -> s.getCreateDate())
                    .select(s -> s.getId())
                    .findFirst()
                    .filter(s -> s.equals(id))
                    .isPresent();
        }

        if (isFirstOnTheSecond) {
            if (verificationCodeEmailEntity.getVerificationCode()
                    .length() == VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH) {
                var email = verificationCodeEmailEntity.getEmail();
                var createDate = verificationCodeEmailEntity.getCreateDate();
                var utcOffset = this.timeZoneUtil.getUtcOffset("UTC");
                var beforeDate = DateUtils.addDays(verificationCodeEmailEntity.getCreateDate(), -1);

                var minVerificationCodeLength = VerificationCodeEmailEnum.MIN_VERIFICATION_CODE_LENGTH;
                isFirstOnTheSecond = this.streamAll(VerificationCodeEmailEntity.class)
                        .where(s -> s.getEmail().equals(email))
                        .where(s -> beforeDate.before(s.getCreateDate()))
                        .where(s -> s.getVerificationCode().length() == minVerificationCodeLength)
                        .where(s -> !s.getHasUsed() || !s.getIsPassed())
                        .where(s -> JPQLFunction
                                .formatDateAsYearMonthDay(s.getCreateDate(), utcOffset)
                                .equals(JPQLFunction.formatDateAsYearMonthDay(createDate, utcOffset)))
                        .where((s, t) -> !t.stream(VerificationCodeEmailEntity.class)
                                .where(m -> m.getEmail().equals(email))
                                .where(m -> beforeDate.before(m.getCreateDate()))
                                .where(m -> m.getVerificationCode().length() == minVerificationCodeLength)
                                .where(m -> JPQLFunction
                                        .formatDateAsYearMonthDay(m.getCreateDate(), utcOffset)
                                        .equals(JPQLFunction.formatDateAsYearMonthDay(createDate, utcOffset)))
                                .where(m -> m.getHasUsed() && !m.getIsPassed())
                                .exists())
                        .sortedBy(s -> s.getId())
                        .sortedBy(s -> s.getCreateDate())
                        .select(s -> s.getId())
                        .findFirst()
                        .filter(s -> s.equals(id))
                        .isPresent();
            }
        }

        return isFirstOnTheSecond;
    }

    @Transactional(readOnly = true)
    public VerificationCodeEmailModel getById(String id) {
        var verificationCodeEmailEntity = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.verificationCodeEmailFormatter.format(verificationCodeEmailEntity);
    }

    public void checkVerificationCodeEmailHasBeenUsed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();

        var verificationCodeEmailEntityOptional = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .findOne();
        if (!verificationCodeEmailEntityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        var verificationCodeEmailEntity = verificationCodeEmailEntityOptional.get();

        if (!this.verificationCodeEmailService
                .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailEntity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (verificationCodeEmailEntity.getHasUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (!verificationCodeEmailEntity.getEmail().equals(verificationCodeEmailModel.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        {
            var expiredDate = DateUtils.addMinutes(new Date(), 5);

            if (!verificationCodeEmailEntity.getCreateDate().before(expiredDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
            }

        }

        verificationCodeEmailEntity.setHasUsed(true);
        this.merge(verificationCodeEmailEntity);
    }

    public void checkVerificationCodeEmailIsPassed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();
        var verificationCodeEmailEntity = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!verificationCodeEmailEntity.getVerificationCode()
                .equals(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        verificationCodeEmailEntity.setIsPassed(true);
        this.merge(verificationCodeEmailEntity);
    }

}
