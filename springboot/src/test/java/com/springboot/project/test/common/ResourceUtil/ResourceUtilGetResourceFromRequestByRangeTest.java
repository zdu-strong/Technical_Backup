package com.springboot.project.test.common.ResourceUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.google.common.net.HttpHeaders;
import com.springboot.project.test.BaseTest;

public class ResourceUtilGetResourceFromRequestByRangeTest extends BaseTest {
	private Resource resource;

	@Test
	public void test() throws IOException {
		var result = this.resourceHttpHeadersUtil.getResourceFromRequest(this.resource.contentLength(), request);
		Assertions.assertEquals(100, result.contentLength());
		Assertions.assertEquals("default.jpg", result.getFilename());
	}

	@BeforeEach
	public void beforeEach() {
		this.resource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
		var storageFileModel = this.storage.storageResource(resource);
		this.request.setRequestURI(storageFileModel.getRelativeUrl());
		this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-99");
	}
}
