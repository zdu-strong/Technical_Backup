package com.springboot.project.test;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.Tika;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.base.Strings;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.permission.TokenUtil;
import com.springboot.project.common.storage.ResourceHttpHeadersUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.scheduled.StorageSpaceScheduled;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.StorageSpaceService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;

/**
 * 
 * @author Me
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

	@Autowired
	protected TestRestTemplate testRestTemplate;

	protected MockHttpServletRequest request = new MockHttpServletRequest();

	@Autowired
	protected StorageSpaceService storageSpaceService;

	@Autowired
	protected StorageSpaceScheduled storageSpaceScheduled;

	@Autowired
	protected Storage storage;

	@Autowired
	protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

	@Autowired
	protected StorageRootPathProperties storageRootPathProperties;

	@Autowired
	protected EncryptDecryptService encryptDecryptService;

	@Autowired
	protected UserService userService;

    @Autowired
	protected UserMessageService userMessageService;

	@Autowired
	protected LongTermTaskService longTermTaskService;

	@Autowired
	protected OrganizeService organizeService;

	@SpyBean
	protected PermissionUtil permissionUtil;

	@Autowired
	protected TokenUtil tokenUtil;

	@Autowired
	protected AuthorizationEmailProperties authorizationEmailProperties;

	@SpyBean
	protected AuthorizationEmailUtil authorizationEmailUtil;

	@Autowired
	protected LongTermTaskUtil longTermTaskUtil;

	@BeforeEach
	public void beforeEachOfBaseTests() {
		Mockito.doNothing().when(this.authorizationEmailUtil).sendVerificationCode(Mockito.anyString());

		String userId = "c3997109-c762-42e9-ad9d-7f6994907ebb";
		String email = "zdu@gmail.com";
		String username = "zdu";

		Mockito.doReturn(true).when(this.permissionUtil).isSignIn(Mockito.anyString());
		Mockito.doReturn(true).when(this.permissionUtil).isSignIn(Mockito.any(HttpServletRequest.class));
		Mockito.doReturn(userId).when(this.permissionUtil).getUserId(Mockito.anyString());
		Mockito.doReturn(userId).when(this.permissionUtil).getUserId(Mockito.any(HttpServletRequest.class));
		Mockito.doReturn(username).when(this.permissionUtil).getUsername(Mockito.anyString());
		Mockito.doReturn(username).when(this.permissionUtil).getUsername(Mockito.any(HttpServletRequest.class));
		Mockito.doReturn(email).when(this.permissionUtil).getEmail(Mockito.anyString());
		Mockito.doReturn(email).when(this.permissionUtil).getEmail(Mockito.any(HttpServletRequest.class));
		Mockito.doNothing().when(this.authorizationEmailUtil).sendVerificationCode(Mockito.anyString());
	}

	protected MultipartFile createTempMultipartFile(Resource resource) {
		try {
			if (resource.isFile()) {
				try (InputStream input = resource.getInputStream()) {
					Tika tika = new Tika();
					return new MockMultipartFile(resource.getFilename(), resource.getFilename(),
							tika.detect(resource.getFilename()), IOUtils.toByteArray(input));
				}
			} else {
				String fileName;
				try {
					resource.getURI();
					fileName = JinqStream.from(new URIBuilder(resource.getFilename()).getPathSegments()).getOnlyValue();
				} catch (IOException e) {
					fileName = resource.getFilename();
				}
				if (Strings.isNullOrEmpty(fileName)) {
					throw new RuntimeException("File name cannot be empty");
				}
				try (InputStream input = resource.getInputStream()) {
					Tika tika = new Tika();
					return new MockMultipartFile(resource.getFilename(), fileName, tika.detect(resource.getFilename()),
							IOUtils.toByteArray(input));
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
