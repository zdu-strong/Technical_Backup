package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
		this.resourceHttpHeadersUtil.setContentType(httpHeaders, request);
		this.resourceHttpHeadersUtil.setContentLength(httpHeaders, totalContentLength, request);
		this.resourceHttpHeadersUtil.setContentDisposition(httpHeaders, ContentDisposition.inline(), request);
		this.resourceHttpHeadersUtil.setContentRangeIfNeed(httpHeaders, totalContentLength, request);

		if (this.resourceHttpHeadersUtil.getRangeList(request).size() > 0) {
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(httpHeaders)
					.body(this.resourceHttpHeadersUtil.getResourceFromRequest(totalContentLength, request));
		} else {
			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(resource);
		}
	}

}
