package com.springboot.project.test.common.BaseTest;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.remote.client.HttpHeaderInterceptor;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.common.EmailUtil.AuthorizationEmailUtil;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;
import com.springboot.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.springboot.project.common.TimeZoneUtil.TimeZoneUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.properties.AliyunCloudStorageProperties;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.properties.IsDevelopmentMockModeProperties;
import com.springboot.project.properties.SchedulingPoolSizeProperties;
import com.springboot.project.properties.ServerAddressProperties;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.UserRoleRelationModel;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.scheduled.MessageScheduled;
import com.springboot.project.scheduled.OrganizeRelationRefreshScheduled;
import com.springboot.project.scheduled.StorageSpaceScheduled;
import com.springboot.project.scheduled.SystemInitScheduled;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.DistributedExecutionTaskService;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.FriendshipService;
import com.springboot.project.service.LoggerService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeRelationService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.StorageSpaceService;
import com.springboot.project.service.SystemRoleService;
import com.springboot.project.service.SystemRoleRelationService;
import com.springboot.project.service.UserRoleService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;

/**
 * 
 * @author Me
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class BaseTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected MockHttpServletRequest request = new MockHttpServletRequest();

    @Autowired
    protected Storage storage;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    protected AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

    @Autowired
    protected OrganizeUtil organizeUtil;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected SchedulingPoolSizeProperties schedulingPoolSizeProperties;

    @Autowired
    protected IsDevelopmentMockModeProperties isDevelopmentMockModeProperties;

    @Autowired
    protected AliyunCloudStorageProperties aliyunCloudStorageProperties;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected ServerAddressProperties serverAddressProperties;

    @Autowired
    protected StorageSpaceService storageSpaceService;

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

    @Autowired
    protected UserEmailService userEmailService;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected FriendshipService friendshipService;

    @Autowired
    protected LoggerService loggerService;

    @Autowired
    protected VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    protected OrganizeRelationService organizeRelationService;

    @Autowired
    protected UserRoleService userRoleService;

    @Autowired
    protected SystemRoleService systemRoleService;

    @Autowired
    protected DistributedExecutionService distributedExecutionService;

    @Autowired
    protected DistributedExecutionTaskService distributedExecutionTaskService;

    @Autowired
    protected SystemRoleRelationService systemRoleRelationService;

    @Autowired
    protected MessageScheduled messageScheduled;

    @Autowired
    protected StorageSpaceScheduled storageSpaceScheduled;

    @SpyBean
    protected OrganizeRelationRefreshScheduled organizeRelationRefreshScheduled;

    @Autowired
    protected SystemInitScheduled systemInitScheduled;

    @BeforeEach
    public void beforeEachOfBaseTest() {
        FileUtils.deleteQuietly(new File(this.storage.getRootPath()));
        new File(this.storage.getRootPath()).mkdirs();
        this.systemInitScheduled.scheduled();
        Mockito.doNothing().when(this.organizeRelationRefreshScheduled).scheduled();
    }

    @SneakyThrows
    protected UserModel createAccount(String email) {
        var password = email;
        if (!hasExistUser(email)) {
            signUp(email, password);
        }
        return signIn(email, password);
    }

    @SneakyThrows
    protected UserModel createAccountOfCompanyAdmin(String email) {
        var password = email;
        if (!hasExistUser(email)) {
            signUp(email, password);
        }
        var company = this.organizeService
                .create(new OrganizeModel().setName(Generators.timeBasedReorderedGenerator().generate().toString()));
        var userInfo = signIn(email, password);
        userInfo.getUserRoleRelationList()
                .addAll(this.userRoleService.getUserRoleListForSuperAdmin()
                        .stream()
                        .map(s -> new UserRoleRelationModel().setUserRole(s))
                        .toList());
        userInfo.getOrganizeRoleRelationList()
                .addAll(this.userRoleService.getOrganizeRoleListByCompanyId(company.getId())
                        .stream()
                        .map(s -> new UserRoleRelationModel().setUserRole(s).setOrganize(company))
                        .toList());
        this.userService.update(userInfo);
        return signIn(email, password);
    }

    @SneakyThrows
    protected UserModel createAccountOfSuperAdmin(String email) {
        var password = email;
        if (!hasExistUser(email)) {
            signUp(email, password);
        }
        var userInfo = signIn(email, password);
        userInfo.getUserRoleRelationList()
                .addAll(this.userRoleService.getUserRoleListForSuperAdmin()
                        .stream()
                        .map(s -> new UserRoleRelationModel().setUserRole(s))
                        .toList());
        this.userService.update(userInfo);
        return signIn(email, password);
    }

    @SneakyThrows
    protected MultipartFile createTempMultipartFile(Resource resource) {
        try (InputStream input = resource.getInputStream()) {
            Tika tika = new Tika();
            return new MockMultipartFile(this.storage.getFileNameFromResource(resource),
                    this.storage.getFileNameFromResource(resource),
                    tika.detect(this.storage.getFileNameFromResource(resource)), IOUtils.toByteArray(input));
        }
    }

    @SneakyThrows
    protected VerificationCodeEmailModel sendVerificationCode(String email) {
        var url = new URIBuilder("/email/send_verification_code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(null),
                VerificationCodeEmailModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    @SneakyThrows
    protected void signOut() {
        var url = new URIBuilder("/sign_out").build();
        var response = this.testRestTemplate.postForEntity(url, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        this.testRestTemplate.getRestTemplate().getInterceptors().clear();
        this.request.removeHeader(HttpHeaders.AUTHORIZATION);
    }

    @SneakyThrows
    private void signUp(String email, String password) {
        var verificationCodeEmail = sendVerificationCode(email);
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp
                .setUsername(email)
                .setPassword(password)
                .setUserEmailList(Lists.newArrayList(
                        new UserEmailModel()
                                .setEmail(email)
                                .setVerificationCodeEmail(verificationCodeEmail)));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private boolean hasExistUser(String email) {
        try {
            this.userService.getUserId(email);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    @SneakyThrows
    private UserModel signIn(String email, String password) {
        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(password);
        var passwordPartList = List.of(new Date(), Generators.timeBasedReorderedGenerator().generate().toString(),
                secretKeyOfAES);
        var passwordPartJsonString = this.objectMapper.writeValueAsString(passwordPartList);
        URI urlForGetPublicKeyOfRSA = new URIBuilder(
                this.testRestTemplate.getRootUri() + "/encrypt_decrypt/rsa/public_key").build();
        var publicKeyOfRSA = new RestTemplate().getForObject(urlForGetPublicKeyOfRSA, String.class);
        var passwordParameter = this.encryptDecryptService.encryptByPublicKeyOfRSA(passwordPartJsonString,
                publicKeyOfRSA);
        var url = new URIBuilder("/sign_in/one_time_password").setParameter("username", email)
                .setParameter("password", passwordParameter)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        this.testRestTemplate.getRestTemplate()
                .setInterceptors(Lists.newArrayList(new HttpHeaderInterceptor(HttpHeaders.AUTHORIZATION,
                        "Bearer " + user.getAccessToken())));
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(user.getAccessToken());
        request.addHeader(HttpHeaders.AUTHORIZATION, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
        return user;
    }

    protected <T> ResponseEntity<T> fromLongTermTask(Supplier<ResponseEntity<String>> supplier,
            ParameterizedTypeReference<T> responseType) {
        return Flowable.fromSupplier(() -> supplier.get())
                .map((response) -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    return response.getBody();
                })
                .doOnNext((encryptedId) -> {
                    while (true) {
                        var url = new URIBuilder(this.testRestTemplate.getRootUri())
                                .setPath("/long_term_task/is_done")
                                .setParameter("encryptedId", encryptedId)
                                .build();
                        var isDone = new RestTemplate().getForObject(url, Boolean.class);
                        if (isDone) {
                            break;
                        }
                        ThreadUtils.sleepQuietly(Duration.ofMillis(1));
                    }
                })
                .map(encryptedId -> {
                    var url = new URIBuilder(this.testRestTemplate.getRootUri()).setPath("/long_term_task")
                            .setParameter("encryptedId", encryptedId).build();
                    var response = new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                            responseType);
                    return response;
                })
                .retry(s -> {
                    if (s.getMessage().contains("The task failed because it stopped")) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .blockingSingle();
    }

}
