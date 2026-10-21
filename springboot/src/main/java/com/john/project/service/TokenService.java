package com.john.project.service;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import com.john.project.entity.TokenEntity;
import com.john.project.entity.UserEntity;
import com.john.project.model.PaginationModel;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.TokenModel;
import cn.hutool.crypto.CryptoException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService extends BaseService {

    @Autowired
    @Lazy
    private EncryptDecryptService encryptDecryptService;

    public String generateAccessToken(String userId) {
        var encryptedPassword = this.uuidUtil.v4();
        var secretKeyOfHash = RandomUtil.randomBytes(64);
        var tokenModel = this.createTokenEntity(userId, encryptedPassword, HexUtil.encodeHexStr(secretKeyOfHash));
        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.HMAC512(secretKeyOfHash));
        return accessToken;
    }

    public String generateAccessToken(String userId, String encryptedPassword) {
        this.checkCorrectPassword(userId, encryptedPassword);
        var secretKeyOfHash = RandomUtil.randomBytes(64);
        var tokenModel = this.createTokenEntity(userId, encryptedPassword, HexUtil.encodeHexStr(secretKeyOfHash));
        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.HMAC512(secretKeyOfHash));
        return accessToken;
    }

    @Transactional(readOnly = true)
    public DecodedJWT getDecodedJWTOfAccessToken(HttpServletRequest request) {
        var accessToken = this.getAccessToken(request);

        var decodedJWT = JWT.decode(accessToken);
        var id = decodedJWT.getId();
        var userId = decodedJWT.getSubject();
        var secretKeyOfHash = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getIsDeleted().equals(false))
                .select(s -> s.getSecretKeyOfHash())
                .findOne()
                .orElse(StringUtils.EMPTY);
        if (StringUtils.isBlank(secretKeyOfHash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
        var decodedJWTOfAccessToken = JWT
                .require(Algorithm.HMAC512(HexUtil.decodeHex(secretKeyOfHash)))
                .build()
                .verify(accessToken);
        return decodedJWTOfAccessToken;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String getEncryptedPassword(String password) {
        var encryptedPassword = this.encryptDecryptService.encryptByPublicKeyOfMLKEM(objectMapper.writeValueAsString(new Object[]{DigestUtils.sha3_512Hex(password), new Date()}));
        return encryptedPassword;
    }

    @SneakyThrows
    public String getPasswordInDatabaseOfEncryptedPassword(String encryptedPassword, String userId) {
        var password = this.getDecryptedPassword(encryptedPassword);
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
        var passwordAfterEncrypted = this.encryptDecryptService.encryptByAES(this.uuidUtil.v4(), secretKeyOfAES);
        return passwordAfterEncrypted;
    }

    public void deleteTokenEntity(String id) {
        var tokenEntity = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        tokenEntity.setIsDeleted(true);
        tokenEntity.setUpdateDate(new Date());
        this.merge(tokenEntity);
    }

    public void deleteTokenByUserId(String userId) {
        var tokenList = this.streamAll(TokenEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getIsDeleted().equals(false))
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .limit(100)
                .toList();
        for (var tokenEntity : tokenList) {
            tokenEntity.setIsDeleted(true);
            tokenEntity.setUpdateDate(new Date());
            this.merge(tokenEntity);
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<TokenModel> searchForInvalidTokenByPagination(long pageNum, long pageSize) {
        var stream = this.streamAll(TokenEntity.class)
                .where(s -> s.getIsDeleted().equals(false))
                .where(s -> s.getUser().getIsDeleted())
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, this.tokenFormatter::format);
    }

    private String getUniqueOneTimePasswordLogo(String encryptedPassword) {
        var logo = HexUtil.encodeHexStr(DigestUtils.sha3_512(encryptedPassword));
        return logo;
    }

    @SneakyThrows
    private void checkCorrectPassword(String userId, String encryptedPassword) {
        try {
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .getOnlyValue();
            var createDate = this.getCreateDateOfEncryptedPassword(encryptedPassword);
            if (createDate.before(DateUtils.addMinutes(new Date(), -5))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            if (createDate.after(DateUtils.addMinutes(new Date(), 5))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            var password = this.getDecryptedPassword(encryptedPassword);
            var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
            var passwordJsonString = this.encryptDecryptService.decryptByAES(userEntity.getPassword(), secretKeyOfAES);
            if (!Validator.isUUID(passwordJsonString)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }

            var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(encryptedPassword);
            var exists = this.streamAll(TokenEntity.class)
                    .where(s -> s.getUser().getId().equals(userId))
                    .where(s -> s.getUniqueOneTimePasswordLogo().equals(uniqueOneTimePasswordLogo))
                    .exists();
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
        } catch (UndeclaredThrowableException | CryptoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Incorrect username or password");
        }
    }

    @SneakyThrows
    private String getDecryptedPassword(String encryptedPassword) {
        var passwordJsonString = this.encryptDecryptService.decryptByByPrivateKeyOfMLKEM(encryptedPassword);
        var password = this.objectMapper.readTree(passwordJsonString).get(0).asText();
        return password;
    }

    @SneakyThrows
    private Date getCreateDateOfEncryptedPassword(String encryptedPassword) {
        var passwordJsonString = this.encryptDecryptService.decryptByByPrivateKeyOfMLKEM(encryptedPassword);
        var createDate = this.objectMapper.treeToValue(this.objectMapper.readTree(passwordJsonString).get(1), Date.class);
        return createDate;
    }

    private String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

    private TokenModel createTokenEntity(String userId, String encryptedPassword, String secretKeyOfHash) {
        var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(encryptedPassword);
        var user = this.streamAll(UserEntity.class).where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(newId());
        tokenEntity.setSecretKeyOfHash(secretKeyOfHash);
        tokenEntity.setUniqueOneTimePasswordLogo(uniqueOneTimePasswordLogo);
        tokenEntity.setUser(user);
        tokenEntity.setIsDeleted(false);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);

        return this.tokenFormatter.format(tokenEntity);
    }

}
