package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class AuthorizationEmailProperties {

    @Value("${spring.mail.username}")
    private String senderEmail;

}
