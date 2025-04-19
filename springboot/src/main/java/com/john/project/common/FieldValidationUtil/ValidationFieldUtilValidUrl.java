package com.john.project.common.FieldValidationUtil;

import com.google.common.collect.Lists;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ValidationFieldUtilValidUrl extends ValidationFieldUtilCorrectFormat {

    public void checkValidOfUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        var request = new MockHttpServletRequest();
        request.setRequestURI(url);
        var relativePath = this.storage.getRelativePathFromRequest(request);
        String folderName = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/")))
                .findFirst()
                .filter(StringUtils::isNotBlank)
                .get();
        var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.REFRESH_STORAGE_SPACE.getValue())
                .setUniqueKey(folderName);
        var hasValid = new AtomicBoolean(false);
        this.longTermTaskUtil.runRetryWhenExists(() -> {
            this.storageSpaceService.update(folderName);
            hasValid.set(this.storageSpaceService.hasValid(folderName));
        }, null, longTermTaskUniqueKeyModel);

        if (!hasValid.get()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is invalid");
        }
    }

}
