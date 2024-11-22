package com.springboot.project.service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.TokenModel;
import cn.hutool.crypto.CryptoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

@Service
public class TokenService extends BaseService {

    public void deleteTokenEntity(String id) {
        var tokenEntity = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        tokenEntity.setIsActive(false);
        this.merge(tokenEntity);
    }

    public String generateAccessToken(String userId) {
        var uniqueOneTimePasswordLogo = this.generateUniqueOneTimePasswordLogo();
        var tokenModel = this.createTokenEntity(uniqueOneTimePasswordLogo, userId);
        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()));
        return accessToken;
    }

    public String generateAccessToken(String userId, String password) {
        this.checkCorrectPassword(password, userId);

        var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(password);
        var tokenModel = this.createTokenEntity(uniqueOneTimePasswordLogo, userId);
        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()));
        return accessToken;
    }

    public String generateNewAccessToken(String accessToken) {
        var uniqueOneTimePasswordLogo = this.generateUniqueOneTimePasswordLogo();
        var decodedJWT = this.getDecodedJWTOfAccessToken(accessToken);
        var userId = decodedJWT.getSubject();
        var tokenModel = this.createTokenEntity(uniqueOneTimePasswordLogo, userId);
        String accessTokenOfNew = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()));
        return accessTokenOfNew;
    }

    @Transactional(readOnly = true)
    public String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

    @Transactional(readOnly = true)
    public DecodedJWT getDecodedJWTOfAccessToken(String accessToken) {
        var decodedJWT = JWT
                .require(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()))
                .build().verify(accessToken);
        if (!this.hasExistTokenEntity(decodedJWT.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
        return decodedJWT;
    }

    @Transactional(readOnly = true)
    public boolean hasExistTokenEntity(String id) {
        var exists = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getIsActive())
                .exists();
        return exists;
    }

    @SneakyThrows
    private String generateUniqueOneTimePasswordLogo() {
        var jsonString = this.objectMapper.writeValueAsString(
                List.of(new Date(), Generators.timeBasedReorderedGenerator().generate().toString()));
        var logo = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        return logo;
    }

    @SneakyThrows
    private String getUniqueOneTimePasswordLogo(String password) {
        var jsonString = this.encryptDecryptService.decryptByByPrivateKeyOfRSA(password);
        var passwordPartList = this.objectMapper.readValue(jsonString, new TypeReference<List<String>>() {
        });
        var logo = Base64.getEncoder().encodeToString(
                this.objectMapper.writeValueAsString(JinqStream.from(passwordPartList).limit(2).toList())
                        .getBytes(StandardCharsets.UTF_8));
        return logo;
    }

    private void checkCorrectPassword(String password, String userId) {
        try {
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .getOnlyValue();
            var jsonString = this.encryptDecryptService.decryptByByPrivateKeyOfRSA(password);
            var passwordPartList = this.objectMapper.readValue(jsonString, new TypeReference<List<String>>() {
            });

            if (!userId.equals(this.encryptDecryptService.decryptByAES(userEntity.getPassword(),
                    JinqStream.from(passwordPartList).skip(2).getOnlyValue()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }

            var passwordDate = FastDateFormat.getInstance(this.dateFormatProperties.getUTC())
                    .parse(JinqStream.from(passwordPartList).limit(1).getOnlyValue());
            if (DateUtils.addMinutes(new Date(), 5).before(passwordDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            if (DateUtils.addMinutes(new Date(), -5).after(passwordDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(password);
            var exists = this.streamAll(TokenEntity.class)
                    .where(s -> s.getUser().getId().equals(userId))
                    .where(s -> s.getUniqueOneTimePasswordLogo().equals(uniqueOneTimePasswordLogo))
                    .exists();
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
        } catch (CryptoException | JsonProcessingException | ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Incorrect username or password");
        }
    }

    private TokenModel createTokenEntity(String uniqueOneTimePasswordLogo, String userId) {
        var user = this.streamAll(UserEntity.class).where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(newId());
        tokenEntity.setUniqueOneTimePasswordLogo(uniqueOneTimePasswordLogo);
        tokenEntity.setUser(user);
        tokenEntity.setIsActive(true);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);

        return this.tokenFormatter.format(tokenEntity);
    }

}
