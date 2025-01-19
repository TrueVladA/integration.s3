package ru.bpmcons.client.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
    private String endPoint;
    private String region;
    private String operativeBucket;
    private String archiveBucket;
    private String expiredTime;
    private String accessKey;
    private String secretKey;
    private int requestRetries = 3;
    private Duration requestDelay = Duration.ofMillis(10);

    private boolean pathStyle = true;
    private int maxConnections = 2000;
    private Duration connectionTimeout = Duration.ofSeconds(4);
    private Duration socketTimeout = Duration.ofSeconds(4);
    private Duration connectionMaxIde = Duration.ofMinutes(1);

    private boolean breakPresign = false;
    private boolean verifyBrokenUrls = false;
}
