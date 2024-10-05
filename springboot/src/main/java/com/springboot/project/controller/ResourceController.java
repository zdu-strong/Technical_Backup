package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URISyntaxException;
import org.jinq.orm.stream.JinqStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.collect.Lists;
import com.springboot.project.common.StorageResource.SequenceResource;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class ResourceController extends BaseController {

    @GetMapping("/resource/**/*")
    public ResponseEntity<?> getResource() throws IOException {
        var resource = this.storage.getResourceFromRequest(request);
        var totalContentLength = resource.contentLength();

        this.resourceHttpHeadersUtil.checkIsCorrectRangeIfNeed(totalContentLength, request);

        HttpHeaders httpHeaders = new HttpHeaders();
        this.resourceHttpHeadersUtil.setETag(httpHeaders, request);
        this.resourceHttpHeadersUtil.setCacheControl(httpHeaders, request);
        this.resourceHttpHeadersUtil.setContentType(httpHeaders, resource, request);
        this.resourceHttpHeadersUtil.setContentLength(httpHeaders, totalContentLength, request);
        this.resourceHttpHeadersUtil.setContentDisposition(httpHeaders, ContentDisposition.inline(), resource, request);
        this.resourceHttpHeadersUtil.setContentRangeIfNeed(httpHeaders, totalContentLength, request);

        if (httpHeaders.getETag().equals(this.request.getHeader(HttpHeaders.IF_NONE_MATCH))) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).headers(httpHeaders).build();
        }

        if (this.resourceHttpHeadersUtil.getRangeList(request).size() > 0) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(httpHeaders)
                    .body(this.resourceHttpHeadersUtil.getResourceFromRequest(totalContentLength, request));
        } else {
            return ResponseEntity.ok().headers(httpHeaders).body(resource);
        }
    }

    @GetMapping("/download/resource/**/*")
    public ResponseEntity<?> downloadResource() throws IOException {
        var resource = this.storage.getResourceFromRequest(request);
        var totalContentLength = resource.contentLength();

        this.resourceHttpHeadersUtil.checkIsCorrectRangeIfNeed(totalContentLength, request);

        HttpHeaders httpHeaders = new HttpHeaders();
        this.resourceHttpHeadersUtil.setETag(httpHeaders, request);
        this.resourceHttpHeadersUtil.setCacheControl(httpHeaders, request);
        this.resourceHttpHeadersUtil.setContentType(httpHeaders, resource, request);
        this.resourceHttpHeadersUtil.setContentLength(httpHeaders, totalContentLength, request);
        this.resourceHttpHeadersUtil.setContentDisposition(httpHeaders, ContentDisposition.attachment(), resource,
                request);
        this.resourceHttpHeadersUtil.setContentRangeIfNeed(httpHeaders, totalContentLength, request);

        if (httpHeaders.getETag().equals(this.request.getHeader(HttpHeaders.IF_NONE_MATCH))) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).headers(httpHeaders).build();
        }

        if (this.resourceHttpHeadersUtil.getRangeList(request).size() > 0) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(httpHeaders)
                    .body(this.resourceHttpHeadersUtil.getResourceFromRequest(totalContentLength, request));
        } else {
            return ResponseEntity.ok().headers(httpHeaders).body(resource);
        }
    }

    @PostMapping("/upload/resource")
    public ResponseEntity<?> uploadResource(@RequestParam MultipartFile file) throws IOException {
        var storageFileModel = this.storage.storageResource(file);
        return ResponseEntity.ok(storageFileModel.getRelativeUrl());
    }

    @PostMapping("/upload/merge")
    public ResponseEntity<?> mergeResource(@RequestBody String[] urlList) throws IOException, URISyntaxException {
        return this.longTermTaskUtil.run(() -> {
            var resourceList = JinqStream.from(Lists.newArrayList(urlList)).select(url -> {
                var mockHttpServletRequest = new MockHttpServletRequest();
                mockHttpServletRequest.setRequestURI(url);
                return mockHttpServletRequest;
            }).select(mockHttpServletRequest -> this.storage.getResourceFromRequest(mockHttpServletRequest)).toList();
            if (resourceList.size() == 1) {
                return ResponseEntity.ok(JinqStream.from(Lists.newArrayList(urlList)).getOnlyValue());
            }

            String fileName = this.storage.getFileNameFromResource(resourceList.stream().findFirst().get());

            var storageFileModel = this.storage.storageResource(new SequenceResource(fileName, resourceList));
            return ResponseEntity.ok(storageFileModel.getRelativeUrl());
        });
    }

    @GetMapping("/is_directory/resource/**/*")
    public ResponseEntity<?> isDirectoryOfResource() throws IOException {
        var isDirectory = this.storage.isDirectory(request);
        return ResponseEntity.ok(isDirectory);
    }

}
