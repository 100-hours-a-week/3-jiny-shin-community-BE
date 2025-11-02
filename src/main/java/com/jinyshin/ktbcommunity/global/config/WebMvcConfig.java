package com.jinyshin.ktbcommunity.global.config;

import com.jinyshin.ktbcommunity.global.interceptor.PostOwnershipInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final PostOwnershipInterceptor postOwnershipInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(postOwnershipInterceptor)
        .order(1)
        .addPathPatterns("/posts/*"); // /posts/{postId}만 매칭
  }
}