package com.jinyshin.ktbcommunity.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:3000") // 프론트엔드 주소
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true) // 쿠키 포함 요청 허용
        .maxAge(3600); // preflight 요청 캐시 시간
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // domain 패키지의 모든 컨트롤러에 /api prefix 자동 추가
    configurer.addPathPrefix("/api",
        c -> c.getPackageName().startsWith("com.jinyshin.ktbcommunity.domain"));
  }
}
