package com.springboot.project.test.common.ResourceUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.BaseTest;

public class ResourceUtilSetContentDispositionTest extends BaseTest {
	private HttpHeaders httpHeaders;

	@Test
	public void test() throws IOException {
		this.resourceHttpHeadersUtil.setContentDisposition(httpHeaders, ContentDisposition.inline(), request);
		Assertions.assertEquals("inline; filename*=UTF-8''default.jpg",
				this.httpHeaders.getContentDisposition().toString());
	}

	@BeforeEach
	public void beforeEach() {
		httpHeaders = new HttpHeaders();
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		this.request.setRequestURI(storageFileModel.getRelativeUrl());
	}
}
