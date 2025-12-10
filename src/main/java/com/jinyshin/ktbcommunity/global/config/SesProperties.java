package com.jinyshin.ktbcommunity.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloud.aws.ses")
public class SesProperties {

  private String fromEmail;
  private String toEmail;
  private boolean enabled = false;
}