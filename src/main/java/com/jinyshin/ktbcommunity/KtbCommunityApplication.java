package com.jinyshin.ktbcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KtbCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(KtbCommunityApplication.class, args);
    }

}
