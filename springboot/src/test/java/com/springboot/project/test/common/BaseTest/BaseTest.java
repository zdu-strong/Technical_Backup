package com.springboot.project.test.common.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.common.OrganizeUtil.OrganizeUtil;
import com.springboot.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.springboot.project.common.utcOffsetUtil.UTCOffsetUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.common.permission.AuthorizationEmailUtil;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.properties.AliyunCloudStorageProperties;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.properties.IsTestOrDevModeProperties;
import com.springboot.project.properties.SchedulingPoolSizeProperties;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.scheduled.MessageScheduled;
import com.springboot.project.scheduled.OrganizeClosureRefreshScheduled;
import com.springboot.project.scheduled.StorageSpaceScheduled;
import com.springboot.project.scheduled.SystemInitScheduled;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.FriendshipService;
import com.springboot.project.service.LoggerService;
import com.springboot.project.service.LongTermTaskCheckService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.OrganizeCheckService;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.StorageSpaceService;
import com.springboot.project.service.TokenService;
import com.springboot.project.service.UserCheckService;
import com.springboot.project.service.UserEmailCheckService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailCheckService;
import com.springboot.project.service.VerificationCodeEmailService;

import io.reactivex.rxjava3.core.Flowable;

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
    protected UTCOffsetUtil utcOffsetUtil;

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
    protected IsTestOrDevModeProperties isTestOrDevModeProperties;

    @Autowired
    protected AliyunCloudStorageProperties aliyunCloudStorageProperties;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected GitProperties gitProperties;

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
    protected OrganizeClosureService organizeClosureService;

    @Autowired
    protected OrganizeCheckService organizeCheckService;

    @Autowired
    protected UserCheckService userCheckService;

    @Autowired
    protected LongTermTaskCheckService longTermTaskCheckService;

    @Autowired
    protected UserEmailCheckService userEmailCheckService;

    @Autowired
    protected VerificationCodeEmailCheckService verificationCodeEmailCheckService;

    @Autowired
    protected MessageScheduled messageScheduled;

    @SpyBean
    protected StorageSpaceScheduled storageSpaceScheduled;

    @SpyBean
    protected OrganizeClosureRefreshScheduled organizeClosureRefreshScheduled;

    @SpyBean
    protected SystemInitScheduled systemInitScheduled;

    @BeforeEach
    public void beforeEachOfBaseTest() throws Throwable {
        FileUtils.deleteQuietly(new File(this.storage.getRootPath()));
        new File(this.storage.getRootPath()).mkdirs();
        Mockito.doNothing().when(this.systemInitScheduled).scheduled();
        Mockito.doNothing().when(this.organizeClosureRefreshScheduled).scheduled();
        Mockito.doNothing().when(this.storageSpaceScheduled).scheduled();
    }

    protected UserModel createAccount(String email) {
        var password = email;
        try {
            if (!hasExistUser(email)) {
                signUp(email, password);
            }
            return signIn(email, password);
        } catch (URISyntaxException | InvalidKeySpecException | NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected MultipartFile createTempMultipartFile(Resource resource) {
        try {
            try (InputStream input = resource.getInputStream()) {
                Tika tika = new Tika();
                return new MockMultipartFile(this.storage.getFileNameFromResource(resource),
                        this.storage.getFileNameFromResource(resource),
                        tika.detect(this.storage.getFileNameFromResource(resource)), IOUtils.toByteArray(input));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected VerificationCodeEmailModel sendVerificationCode(String email) throws URISyntaxException {
        var url = new URIBuilder("/email/send_verification_code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(null),
                VerificationCodeEmailModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private void signUp(String email, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, JsonProcessingException {
        var verificationCodeEmail = sendVerificationCode(email);
        var keyPairOfRSA = this.encryptDecryptService.generateKeyPairOfRSA();
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp.setUsername(email)
                .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                        .setVerificationCodeEmail(verificationCodeEmail)))
                .setPublicKeyOfRSA(keyPairOfRSA.getPublicKeyOfRSA());
        userModelOfSignUp
                .setPrivateKeyOfRSA(this.encryptDecryptService.encryptByAES(
                        keyPairOfRSA.getPrivateKeyOfRSA(),
                        this.encryptDecryptService.generateSecretKeyOfAES(password + password)));
        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(password);
        userModelOfSignUp.setPassword(this.encryptDecryptService.encryptByAES(secretKeyOfAES, secretKeyOfAES));
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private boolean hasExistUser(String email) {
        try {
            this.userService.getUserWithMoreInformation(email);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private UserModel signIn(String email, String password)
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonMappingException,
            JsonProcessingException {
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
        var url = new URIBuilder("/sign_in").setParameter("username", email)
                .setParameter("password", passwordParameter)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        this.testRestTemplate.getRestTemplate()
                .setInterceptors(Lists.newArrayList(new HttpHeaderInterceptor(HttpHeaders.AUTHORIZATION,
                        "Bearer " + user.getAccessToken())));
        user.setPrivateKeyOfRSA(
                this.encryptDecryptService.decryptByAES(user.getPrivateKeyOfRSA(),
                        this.encryptDecryptService.generateSecretKeyOfAES(password + password)));
        return user;
    }

    protected <T> ResponseEntity<T> fromLongTermTask(Supplier<ResponseEntity<String>> supplier,
            ParameterizedTypeReference<T> responseType) {
        return Flowable.fromSupplier(() -> supplier.get())
                .concatMap((response) -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    return Flowable.just(response.getBody());
                }).concatMap((id) -> {
                    while (true) {
                        var url = new URIBuilder(this.testRestTemplate.getRootUri()).setPath("/long_term_task/is_done")
                                .setParameter("id", id).build();
                        var isDone = new RestTemplate().getForObject(url, Boolean.class);
                        if (isDone) {
                            break;
                        }
                        Thread.sleep(1);
                    }
                    return Flowable.just(id);
                }).concatMap(id -> {
                    var url = new URIBuilder(this.testRestTemplate.getRootUri()).setPath("/long_term_task")
                            .setParameter("id", id).build();
                    var response = new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                            responseType);
                    return Flowable.just(response);
                }).retry(s -> {
                    if (s.getMessage().contains("The task failed because it stopped")) {
                        return true;
                    } else if (s instanceof InterruptedException) {
                        return true;
                    } else {
                        return false;
                    }
                }).blockingSingle();
    }

}
