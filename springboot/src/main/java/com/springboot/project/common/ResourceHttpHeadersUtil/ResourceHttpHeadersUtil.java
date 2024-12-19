package com.springboot.project.common.ResourceHttpHeadersUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.tika.Tika;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ContentDisposition.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ResponseStatusException;
import com.google.common.collect.Lists;
import com.springboot.project.common.StorageResource.SequenceResource;
import com.springboot.project.common.storage.Storage;
import cn.hutool.core.text.StrFormatter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

@Component
public class ResourceHttpHeadersUtil {

    @Autowired
    private Storage storage;

    private String boundary = MimeTypeUtils.generateMultipartBoundaryString();
    private Tika tika = new Tika();

    public void setContentRangeIfNeed(HttpHeaders httpHeaders, long totalContentLength, HttpServletRequest request) {
        var rangeList = this.getRangeList(request);
        if (rangeList.size() == 1) {
            var range = JinqStream.from(rangeList).getOnlyValue();
            var start = range.getRangeStart(totalContentLength);
            var end = range.getRangeEnd(totalContentLength);
            httpHeaders.set(HttpHeaders.CONTENT_RANGE, "bytes " + String.valueOf(start) + "-" + String.valueOf(end)
                    + "/" + String.valueOf(totalContentLength));
        }
    }

    public void setETag(HttpHeaders httpHeaders, HttpServletRequest request) {
        var eTag = StrFormatter.format("\"{}\"",
                Base64.getEncoder().encodeToString(
                        this.storage.getRelativePathFromRequest(request).getBytes(StandardCharsets.UTF_8)));
        httpHeaders
                .setETag(eTag);
    }

    public void setCacheControl(HttpHeaders httpHeaders, HttpServletRequest request) {
        httpHeaders.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic().noTransform());
    }

    @SneakyThrows
    public void setContentType(HttpHeaders httpHeaders, Resource resource, HttpServletRequest request) {
        var rangeList = this.getRangeList(request);
        if (rangeList.size() > 1) {
            httpHeaders.setContentType(MediaType.parseMediaType("multipart/byteranges; boundary=" + this.boundary));
            return;
        }

        if (resource instanceof ByteArrayResource) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        } else {
            ArrayList<String> pathSegments = Lists
                    .newArrayList(new URIBuilder(request.getRequestURI()).getPathSegments());
            Collections.reverse(pathSegments);
            String fileName = pathSegments.stream().findFirst().get();
            httpHeaders.setContentType(MediaType.parseMediaType(this.tika.detect(fileName)));
        }
    }

    @SneakyThrows
    public void setContentLength(HttpHeaders httpHeaders, long totalContentLength, HttpServletRequest request) {
        var rangeList = this.getRangeList(request);
        if (rangeList.size() > 1) {
            long contentLength = this.getResourceFromRequest(totalContentLength, request).contentLength();
            httpHeaders.setContentLength(contentLength);
        } else if (rangeList.size() == 1) {
            var range = JinqStream.from(rangeList).getOnlyValue();
            var start = range.getRangeStart(totalContentLength);
            var end = range.getRangeEnd(totalContentLength);
            httpHeaders.setContentLength(end - start + 1);
        } else {
            httpHeaders.setContentLength(totalContentLength);
        }
    }

    @SneakyThrows
    public void setContentDisposition(HttpHeaders httpHeaders, Builder contentDispositionBuilder,
            Resource resource,
            HttpServletRequest request) {
        var pathSegments = Lists.newArrayList(new URIBuilder(request.getRequestURI()).getPathSegments());
        Collections.reverse(pathSegments);
        String fileName = pathSegments.stream().findFirst().get();
        ContentDisposition contentDisposition = contentDispositionBuilder.filename(fileName, StandardCharsets.UTF_8)
                .build();
        httpHeaders.setContentDisposition(contentDisposition);
    }

    public List<HttpRange> getRangeList(HttpServletRequest request) {
        var tempHttpHeaders = new HttpHeaders();
        tempHttpHeaders.set(HttpHeaders.RANGE, request.getHeader(HttpHeaders.RANGE));
        return tempHttpHeaders.getRange();
    }

    @SneakyThrows
    public Resource getResourceFromRequest(long totalContentLength, HttpServletRequest request) {
        var rangeList = this.getRangeList(request);
        if (rangeList.size() > 1) {
            ArrayList<String> pathSegments = Lists
                    .newArrayList(new URIBuilder(request.getRequestURI()).getPathSegments());
            Collections.reverse(pathSegments);
            String fileName = pathSegments.stream().findFirst().get();
            MediaType mediaType = MediaType.parseMediaType(this.tika.detect(fileName));
            var resourceListOne = JinqStream.from(rangeList).selectAllList(range -> {
                var start = range.getRangeStart(totalContentLength);
                var end = range.getRangeEnd(totalContentLength);
                var resourceTwo = this.storage.getResourceFromRequest(request, start, end - start + 1);
                var textContentBuilder = new StringBuilder();
                textContentBuilder.append("\n");
                textContentBuilder.append("--" + this.boundary);
                textContentBuilder.append("\n");
                textContentBuilder.append(HttpHeaders.CONTENT_TYPE);
                textContentBuilder.append(": ");
                textContentBuilder.append(mediaType.toString());
                textContentBuilder.append("\n");
                textContentBuilder.append(HttpHeaders.CONTENT_RANGE);
                textContentBuilder.append(": ");
                textContentBuilder.append("bytes ");
                textContentBuilder.append(String.valueOf(start));
                textContentBuilder.append("-");
                textContentBuilder.append(String.valueOf(end));
                textContentBuilder.append("/");
                textContentBuilder.append(String.valueOf(totalContentLength));
                textContentBuilder.append("\n");
                textContentBuilder.append("\n");
                var resourceOne = new ByteArrayResource(
                        textContentBuilder.toString().getBytes(StandardCharsets.UTF_8));
                return Lists.newArrayList(resourceOne, resourceTwo);
            }).toList();
            var resourceListTwo = new ArrayList<Resource>();
            resourceListTwo.addAll(resourceListOne);
            var textContentOfEnd = "\n--" + this.boundary + "--\n";
            resourceListTwo.add(new ByteArrayResource(textContentOfEnd.getBytes(StandardCharsets.UTF_8)));
            return new SequenceResource(fileName, resourceListTwo);
        } else if (rangeList.size() == 1) {
            var range = JinqStream.from(rangeList).getOnlyValue();
            var start = range.getRangeStart(totalContentLength);
            var end = range.getRangeEnd(totalContentLength);
            return this.storage.getResourceFromRequest(request, start, end - start + 1);
        } else {
            return this.storage.getResourceFromRequest(request);
        }
    }

    public void checkIsCorrectRangeIfNeed(long totalContentLength, HttpServletRequest request) {
        List<HttpRange> rangeList;
        try {
            rangeList = this.getRangeList(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, e.getMessage());
        }

        if (rangeList.size() > 0) {
            for (var range : rangeList) {
                var start = range.getRangeStart(totalContentLength);
                var end = range.getRangeEnd(totalContentLength);
                if (start > totalContentLength || end > totalContentLength) {
                    throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                            HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.getReasonPhrase());
                }
                if (totalContentLength == 0) {
                    if (end != -1) {
                        throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                                HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.getReasonPhrase());
                    }
                } else {
                    if (end < start) {
                        throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                                HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.getReasonPhrase());
                    }
                }
            }
        }
    }
}
