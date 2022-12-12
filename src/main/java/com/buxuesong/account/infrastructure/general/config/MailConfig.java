package com.buxuesong.account.infrastructure.general.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfig {
    private String username;
    private String password;
    private String smtpPort;
    private String smtpHost;
    private String toAddress;
    private String dbRealPath;
}
