package com.jinyshin.ktbcommunity.global.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cors")
public class WebMvcConfig implements WebMvcConfigurer {

  private List<String> allowedOrigins = new ArrayList<>();

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(allowedOrigins.toArray(new String[0]))
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // domain 패키지의 모든 컨트롤러에 /api prefix 자동 추가
    configurer.addPathPrefix("/api",
        c -> c.getPackageName().startsWith("com.jinyshin.ktbcommunity.domain"));
  }
}
