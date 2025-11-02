package com.jinyshin.ktbcommunity.global.config;

import com.jinyshin.ktbcommunity.domain.post.entity.Post;
import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.domain.user.entity.User;
import com.jinyshin.ktbcommunity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class SeedConfig {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner seedRunner() {
    return args -> seed();
  }

  @Transactional
  void seed() {
    if (userRepository.count() >= 2) {
      log.info("[SeedConfig] Test users already exist. Skip seeding.");
      return;
    }

    // User A 생성
    User userA = new User("testerA@test.com", "TesterA", passwordEncoder.encode("password123"));
    userRepository.save(userA);
    log.info("[SeedConfig] Created User A: email={}, nickname={}", userA.getEmail(),
        userA.getNickname());

    // User A의 게시글 2개
    Post postA1 = new Post("User A의 첫 번째 게시글", "이것은 User A가 작성한 첫 번째 게시글입니다.", userA);
    postRepository.save(postA1);
    log.info("[SeedConfig] Created Post A1: postId={}, title={}", postA1.getPostId(),
        postA1.getTitle());

    Post postA2 = new Post("User A의 두 번째 게시글", "이것은 User A가 작성한 두 번째 게시글입니다.", userA);
    postRepository.save(postA2);
    log.info("[SeedConfig] Created Post A2: postId={}, title={}", postA2.getPostId(),
        postA2.getTitle());

    // User B 생성
    User userB = new User("testerB@test.com", "TesterB", passwordEncoder.encode("password123"));
    userRepository.save(userB);
    log.info("[SeedConfig] Created User B: email={}, nickname={}", userB.getEmail(),
        userB.getNickname());

    // User B의 게시글 2개
    Post postB1 = new Post("User B의 첫 번째 게시글", "이것은 User B가 작성한 첫 번째 게시글입니다.", userB);
    postRepository.save(postB1);
    log.info("[SeedConfig] Created Post B1: postId={}, title={}", postB1.getPostId(),
        postB1.getTitle());

    Post postB2 = new Post("User B의 두 번째 게시글", "이것은 User B가 작성한 두 번째 게시글입니다.", userB);
    postRepository.save(postB2);
    log.info("[SeedConfig] Created Post B2: postId={}, title={}", postB2.getPostId(),
        postB2.getTitle());

    log.info("[SeedConfig] Seed completed. Total users: {}, Total posts: {}",
        userRepository.count(), postRepository.count());
  }
}