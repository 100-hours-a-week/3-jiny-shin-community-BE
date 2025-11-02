//package com.jinyshin.ktbcommunity.global.config;
//
//import com.jinyshin.ktbcommunity.global.filter.SessionAuthFilter;
//import jakarta.servlet.Filter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebFilterConfig {
//
//  private final SessionAuthFilter sessionAuthFilter;
//
//  @Bean
//  public FilterRegistrationBean<Filter> sessionFilter() {
//    FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
//    bean.setFilter(sessionAuthFilter);
//    bean.addUrlPatterns("/*");
//    bean.setOrder(1);
//
//    return bean;
//  }
//}
