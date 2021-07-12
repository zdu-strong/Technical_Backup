package com.springboot.project.test.controller.ResourceController;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.springboot.project.test.BaseTest;

public class ResourceControllerGetResourceTest extends BaseTest {

	private String resourceUrl;

	@Test
	public void test() throws URISyntaxException {
		URI url = new URIBuilder(resourceUrl).build();
		var response = this.testRestTemplate.getForEntity(url, byte[].class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
		Assertions.assertTrue(response.getHeaders().getContentDisposition().isInline());
		Assertions.assertEquals("default.jpg", response.getHeaders().getContentDisposition().getFilename());
		Assertions.assertEquals(StandardCharsets.UTF_8, response.getHeaders().getContentDisposition().getCharset());
		Assertions.assertEquals(9287, response.getBody().length);
		Assertions.assertNotNull(response.getHeaders().getETag());
		Assertions.assertTrue(response.getHeaders().getETag().startsWith("\""));
		Assertions.assertEquals("max-age=604800, no-transform, public", response.getHeaders().getCacheControl());
		Assertions.assertEquals(9287, response.getHeaders().getContentLength());
	}

	@BeforeEach
	public void beforeEach() {
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		this.resourceUrl = storageFileModel.getRelativeUrl();
	}
}
