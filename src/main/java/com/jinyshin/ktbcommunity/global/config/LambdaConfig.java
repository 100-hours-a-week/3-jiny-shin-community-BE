package com.jinyshin.ktbcommunity.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;

@Configuration
@RequiredArgsConstructor
public class LambdaConfig {

  private final S3Properties s3Properties;

  @Bean
  public LambdaAsyncClient lambdaAsyncClient() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3Properties.getAccessKey(),
        s3Properties.getSecretKey()
    );

    return LambdaAsyncClient.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }
}