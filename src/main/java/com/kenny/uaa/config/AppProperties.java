package com.kenny.uaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;

//kenny.jwt.accessTokenExpireTime
@ConfigurationProperties(prefix = "kenny")
@Configuration
public class AppProperties {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    private Ali ali = new Ali();

    @Getter
    @Setter
    @Valid
    private LeanCloud leanCloud = new LeanCloud();

    @Getter
    @Setter
    private SmsProvider smsProvider = new SmsProvider();

    @Getter
    @Setter
    @Valid
    private EmailProvider emailProvider = new EmailProvider();

    @Getter
    @Setter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        private Long accessTokenExpireTime = 60_000L;
        private Long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L;
    }

    @Getter
    @Setter
    public static class LeanCloud {
        private String appId;
        private String appKey;
    }

    @Getter
    @Setter
    public static class Ali {
        private String apiKey;
        private String apiSecret;
    }

    @Getter
    @Setter
    public static class SmsProvider {
        private String name;
        private String apiUrl;
    }

    @Getter
    @Setter
    public static class EmailProvider {
        private String name;
        private String apiKey;
    }
}
