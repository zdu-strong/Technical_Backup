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
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.TokenModel;
import cn.hutool.crypto.CryptoException;

@Service
public class TokenService extends BaseService {

    public String generateAccessToken(String userId) {
        var jwtId = newId();
        var encryptedPassword = this.uuidUtil.v4();
        var secretKeyOfHash = RandomUtil.randomBytes(64);
        var accessToken = JWT.create()
                .withSubject(userId)
                .withJWTId(jwtId)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC512(secretKeyOfHash));
        var hashOfAccessToken = DigestUtils.sha3_512Hex(accessToken);
        this.createTokenEntity(jwtId, userId, encryptedPassword, hashOfAccessToken);
        return accessToken;
    }

    public String generateAccessToken(String userId, String encryptedPassword) {
        this.checkCorrectPassword(userId, encryptedPassword);
        var jwtId = newId();
        var secretKeyOfHash = RandomUtil.randomBytes(64);
        var accessToken = JWT.create()
                .withSubject(userId)
                .withJWTId(jwtId)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC512(secretKeyOfHash));
        var hashOfAccessToken = DigestUtils.sha3_512Hex(accessToken);
        this.createTokenEntity(jwtId, userId, encryptedPassword, hashOfAccessToken);
        return accessToken;
    }

    @Transactional(readOnly = true)
    public boolean getIsValidOfAccessToken(String accessToken) {
        var decodedJWT = JWT.decode(accessToken);
        var jwtId = decodedJWT.getId();
        var hashOfAccessToken = DigestUtils.sha3_512Hex(accessToken);
        var isValid = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(jwtId))
                .where(s -> s.getHashOfAccessToken().equals(hashOfAccessToken))
                .where(s -> s.getIsDeleted().equals(false))
                .exists();
        return isValid;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String getEncryptedPassword(String password) {
        var encryptedPassword = this.encryptDecryptUtil.encryptByPublicKeyOfMLKEM(objectMapper.writeValueAsString(new Object[]{DigestUtils.sha3_512Hex(password), new Date()}));
        return encryptedPassword;
    }

    @SneakyThrows
    public String getPasswordInDatabaseOfEncryptedPassword(String encryptedPassword, String userId) {
        var password = this.getDecryptedPassword(encryptedPassword);
        var secretKeyOfAES = this.encryptDecryptUtil.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
        var passwordAfterEncrypted = this.encryptDecryptUtil.encryptByAES(this.uuidUtil.v4(), secretKeyOfAES);
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
            var secretKeyOfAES = this.encryptDecryptUtil.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
            var passwordJsonString = this.encryptDecryptUtil.decryptByAES(userEntity.getPassword(), secretKeyOfAES);
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
        var passwordJsonString = this.encryptDecryptUtil.decryptByByPrivateKeyOfMLKEM(encryptedPassword);
        var password = this.objectMapper.readTree(passwordJsonString).get(0).asText();
        return password;
    }

    @SneakyThrows
    private Date getCreateDateOfEncryptedPassword(String encryptedPassword) {
        var passwordJsonString = this.encryptDecryptUtil.decryptByByPrivateKeyOfMLKEM(encryptedPassword);
        var createDate = this.objectMapper.treeToValue(this.objectMapper.readTree(passwordJsonString).get(1), Date.class);
        return createDate;
    }

    private void createTokenEntity(String id, String userId, String encryptedPassword, String hashOfAccessToken) {
        var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(encryptedPassword);
        var user = this.streamAll(UserEntity.class).where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(id);
        tokenEntity.setHashOfAccessToken(hashOfAccessToken);
        tokenEntity.setUniqueOneTimePasswordLogo(uniqueOneTimePasswordLogo);
        tokenEntity.setUser(user);
        tokenEntity.setIsDeleted(false);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);
    }

}
