package com.jinyshin.ktbcommunity.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@Slf4j
public class S3Config {

  @Bean
  public S3Client s3Client(S3Properties s3Properties) {
    S3ClientBuilder builder = S3Client.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey())));

    S3Client s3Client = builder.build();
    log.info("S3 Client initialized for region: {}", s3Properties.getRegion());
    return s3Client;
  }
}
