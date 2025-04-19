package com.john.project.common.FieldValidationUtil;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
        this.storageSpaceService.update(folderName);
        var hasValid = this.storageSpaceService.hasValid(folderName);
        if (!hasValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is invalid");
        }
    }

}
