package com.springboot.project.test.controller.UploadResourceController;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import com.springboot.project.test.BaseTest;

public class UploadResourceControllerTest extends BaseTest {

	@Test
	public void test() throws URISyntaxException {
		var url = new URIBuilder("/upload/resource").build();
		var body = new LinkedMultiValueMap<Object, Object>();
		body.set("file", new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		var response = this.testRestTemplate.postForEntity(url, body, String.class);
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
}
