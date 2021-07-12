package com.springboot.project.test.controller.UploadResourceController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import com.springboot.project.common.storage.RangeUrlResource;
import com.springboot.project.test.BaseTest;

import io.reactivex.Observable;

public class UploadResourceControllerUploadMergeTest extends BaseTest {
	private List<String> urlList;

	@Test
	public void test() throws URISyntaxException {
		var url = new URIBuilder("/upload/merge").build();
		var response = this.testRestTemplate.postForEntity(url, urlList, String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertTrue(response.getBody().startsWith("/resource/"));
		var result = this.testRestTemplate.getForEntity(new URIBuilder(response.getBody()).build(), byte[].class);
		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertEquals(MediaType.IMAGE_JPEG, result.getHeaders().getContentType());
		Assertions.assertTrue(result.getHeaders().getContentDisposition().isInline());
		Assertions.assertEquals("default.jpg", result.getHeaders().getContentDisposition().getFilename());
		Assertions.assertEquals(StandardCharsets.UTF_8, result.getHeaders().getContentDisposition().getCharset());
		Assertions.assertEquals(9287, result.getBody().length);
		Assertions.assertNotNull(result.getHeaders().getETag());
		Assertions.assertTrue(result.getHeaders().getETag().startsWith("\""));
		Assertions.assertEquals("max-age=604800, no-transform, public", result.getHeaders().getCacheControl());
		Assertions.assertEquals(9287, result.getHeaders().getContentLength());
	}

	@BeforeEach
	public void beforeEach() throws IOException {
		var imageResource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
		var everySize = 100;
		this.urlList = Observable
				.range(0,
						Double.valueOf(Math.ceil(Double.valueOf(imageResource.contentLength()) / everySize)).intValue())
				.map(startIndex -> {
					var url = new URIBuilder("/upload/resource").build();
					var body = new LinkedMultiValueMap<Object, Object>();
					var rangeLength = everySize;
					if (imageResource.contentLength() < startIndex * everySize + everySize) {
						rangeLength = Long.valueOf(imageResource.contentLength() - startIndex * everySize).intValue();
					}
					body.set("file", new RangeUrlResource(ClassLoader.getSystemResource("image/default.jpg"),
							startIndex * everySize, rangeLength));
					var response = this.testRestTemplate.postForEntity(url, body, String.class);
					Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
					return this.testRestTemplate.getRootUri() + response.getBody();
				}).toList().blockingGet();
	}
}
