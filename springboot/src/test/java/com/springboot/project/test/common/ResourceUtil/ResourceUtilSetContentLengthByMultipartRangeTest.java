package com.springboot.project.test.common.ResourceUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.BaseTest;

public class ResourceUtilSetContentLengthByMultipartRangeTest extends BaseTest {
	private HttpHeaders httpHeaders;
	private Resource resource;

	@Test
	public void test() throws IOException {
		this.resourceHttpHeadersUtil.setContentLength(httpHeaders, resource.contentLength(), request);
		Assertions.assertEquals(9287, resource.contentLength());
		Assertions.assertTrue(this.httpHeaders.getContentLength() > 300);
	}

	@BeforeEach
	public void beforeEach() {
		httpHeaders = new HttpHeaders();
		this.resource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
		var storageFileModel = this.storage.storageResource(this.resource);
		this.request.setRequestURI(storageFileModel.getRelativeUrl());
		this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-99,400-499,1900-1999");
	}
}
