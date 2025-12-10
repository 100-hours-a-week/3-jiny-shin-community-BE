package com.jinyshin.ktbcommunity.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@EnableConfigurationProperties(SesProperties.class)
@Slf4j
public class SesConfig {

  @Bean
  @ConditionalOnProperty(name = "cloud.aws.ses.enabled", havingValue = "true", matchIfMissing = false)
  public SesClient sesClient(S3Properties s3Properties) {
    SesClient sesClient = SesClient.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey())))
        .build();

    log.info("SES Client initialized for region: {}", s3Properties.getRegion());
    return sesClient;
  }
}