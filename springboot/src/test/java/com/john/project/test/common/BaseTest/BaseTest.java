package com.john.project.test.common.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import com.john.project.common.DistributedExecution.NonceCleanDistributedExecution;
import com.john.project.common.DistributedExecution.OrganizeClosureRefreshDistributedExecution;
import com.john.project.common.DistributedExecution.StorageSpaceCleanDistributedExecution;
import com.john.project.common.EncryptDecryptUtil.EncryptDecryptUtil;
import com.john.project.common.FieldValidationUtil.ValidationFieldUtil;
import com.john.project.common.uuid.UUIDUtil;
import com.john.project.enums.OrganizeTypeEnum;
import com.john.project.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.collect.Lists;
import com.john.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.OrganizeUtil.OrganizeUtil;
import com.john.project.common.ResourceHttpHeadersUtil.ResourceHttpHeadersUtil;
import com.john.project.common.TimeZoneUtil.TimeZoneUtil;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.properties.AliyunCloudStorageProperties;
import com.john.project.properties.AuthorizationEmailProperties;
import com.john.project.properties.DevelopmentMockModeProperties;
import com.john.project.properties.SchedulingPoolSizeProperties;
import com.john.project.properties.ServerAddressProperties;
import com.john.project.properties.StorageRootPathProperties;
import com.john.project.common.storage.Storage;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.scheduled.MessageScheduled;
import com.john.project.scheduled.SystemInitScheduled;
import com.john.project.service.DistributedExecutionMainService;
import com.john.project.service.DistributedExecutionDetailService;
import com.john.project.service.FriendshipService;
import com.john.project.service.LoggerService;
import com.john.project.service.LongTermTaskService;
import com.john.project.service.OrganizeRelationService;
import com.john.project.service.OrganizeService;
import com.john.project.service.StorageSpaceService;
import com.john.project.service.PermissionService;
import com.john.project.service.PermissionRelationService;
import com.john.project.service.RoleService;
import com.john.project.service.TokenService;
import com.john.project.service.UserEmailService;
import com.john.project.service.UserMessageService;
import com.john.project.service.UserRoleRelationService;
import com.john.project.service.UserService;
import com.john.project.service.VerificationCodeEmailService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.resttestclient.TestRestTemplate;

/**
 * @author Me
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureTestRestTemplate
public abstract class BaseTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected RestTemplate restTemplate;

    protected final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    protected MockHttpServletRequest request = new MockHttpServletRequest();

    @Autowired
    protected Storage storage;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected NonceCleanDistributedExecution nonceCleanDistributedExecution;

    @Autowired
    protected OrganizeClosureRefreshDistributedExecution organizeClosureRefreshDistributedExecution;

    @Autowired
    protected StorageSpaceCleanDistributedExecution storageSpaceCleanDistributedExecution;

    @Autowired
    protected ResourceHttpHeadersUtil resourceHttpHeadersUtil;

    @Autowired
    protected UUIDUtil uuidUtil;

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
    protected DistributedExecutionUtil distributedExecutionUtil;

    @Autowired
    protected ValidationFieldUtil validationFieldUtil;

    @Autowired
    protected EncryptDecryptUtil encryptDecryptUtil;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected SchedulingPoolSizeProperties schedulingPoolSizeProperties;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @Autowired
    protected AliyunCloudStorageProperties aliyunCloudStorageProperties;

    @Autowired
    protected AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    protected GitProperties gitProperties;

    @Autowired
    protected ServerAddressProperties serverAddressProperties;

    @Autowired
    protected StorageSpaceService storageSpaceService;

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
    protected RoleService roleService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected DistributedExecutionMainService distributedExecutionMainService;

    @Autowired
    protected DistributedExecutionDetailService distributedExecutionDetailService;

    @Autowired
    protected PermissionRelationService permissionRelationService;

    @Autowired
    protected UserRoleRelationService userRoleRelationService;

    @Autowired
    protected MessageScheduled messageScheduled;

    @Autowired
    protected SystemInitScheduled systemInitScheduled;

    @BeforeEach
    public void beforeEachOfBaseTest() {
        FileUtils.deleteQuietly(new File(this.storage.getRootPath()));
        new File(this.storage.getRootPath()).mkdirs();
        this.systemInitScheduled.scheduled();
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
        var userModel = createAccountOfSuperAdmin(email);
        {
            var url = new URIBuilder("/organize/create").build();
            var response = this.testRestTemplate.postForEntity(url,
                    new OrganizeModel()
                            .setName(uuidUtil.v4())
                            .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue()),
                    OrganizeModel.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            this.initOrganizeRoleList(response.getBody());
            var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
            superAdminRoleQueryPaginationModel.setPageNum(1L);
            superAdminRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
            var roleList = this.roleService
                    .searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel)
                    .getItems();
            userModel.getRoleList().addAll(roleList);
            this.userService.update(userModel);
        }
        return signIn(email, email);
    }

    @SneakyThrows
    protected UserModel createAccountOfSuperAdmin(String email) {
        var userModel = createAccount(email);
        {
            var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
            superAdminRoleQueryPaginationModel.setPageNum(1L);
            superAdminRoleQueryPaginationModel.setPageSize(200L);
            var roleList = this.roleService
                    .searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel)
                    .getItems();
            userModel.getRoleList().addAll(roleList);
            this.userService.update(userModel);
        }
        return signIn(email, email);
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
        var url = new URIBuilder("/email/send-verification-code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, null,
                VerificationCodeEmailModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    @SneakyThrows
    protected void signOut() {
        var url = new URIBuilder("/sign-out").build();
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
                .setPassword(this.tokenService.getEncryptedPassword(password))
                .setUserEmailList(Lists.newArrayList(
                        new UserEmailModel()
                                .setEmail(email)
                                .setVerificationCodeEmail(verificationCodeEmail)));
        var url = new URIBuilder("/sign-up/rsa/one-time").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                Object.class);
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
        var url = new URIBuilder("/sign-in/rsa/one-time")
                .setParameter("username", email)
                .setParameter("password", this.tokenService.getEncryptedPassword(password))
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        this.testRestTemplate.getRestTemplate().setInterceptors(
                Lists.newArrayList(
                        (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
                            HttpHeaders headers = request.getHeaders();
                            if (!headers.containsHeader("Authorization")) {
                                headers.setBearerAuth(user.getAccessToken());
                            }
                            return execution.execute(request, body);
                        }
                )
        );
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(user.getAccessToken());
        request.addHeader(HttpHeaders.AUTHORIZATION, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
        return user;
    }

    protected void initOrganizeRoleList(OrganizeModel organize) {
        var organizeRoleList = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> s.getIsOrganizeRole())
                .toList();
        for (var organizeRole : organizeRoleList) {
            this.roleService.create(new RoleModel()
                    .setName(organizeRole.getValue())
                    .setPermissionList(organizeRole.getPermissionList().stream()
                            .map(s -> new PermissionRelationModel()
                                    .setPermission(s.getValue())
                                    .setOrganize(organize)
                            )
                            .toList()));
        }
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
                        var url = new URIBuilder(this.serverAddressProperties.getServerAddress())
                                .setPath("/long-term-task/is-done")
                                .setParameter("encryptedId", encryptedId)
                                .build();
                        var isDone = restTemplate.getForObject(url, Boolean.class);
                        if (isDone) {
                            break;
                        }
                        ThreadUtils.sleepQuietly(Duration.ofMillis(1));
                    }
                })
                .map(encryptedId -> {
                    var url = new URIBuilder(this.serverAddressProperties.getServerAddress())
                            .setPath("/long-term-task")
                            .setParameter("encryptedId", encryptedId)
                            .build();
                    var response = restTemplate.exchange(url, HttpMethod.GET, null,
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
