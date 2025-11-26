package com.jinyshin.ktbcommunity.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("KTB Community API")
            .version("v1.0")
            .description("KTB 커뮤니티 백엔드 API 문서")
            .contact(new Contact()
                .name("Jiny.shin")
                .email("jinyshin.dev@gmail.com")))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("로컬 개발 서버"),
            new Server().url("https://ktb.community.jinyshin.cloud/api").description("프로덕션 서버")
        ));
  }
}