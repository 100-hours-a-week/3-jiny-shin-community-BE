package com.jinyshin.ktbcommunity.domain.user.repository;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<User> findByEmail(String email);

  Optional<User> findByUserIdAndDeletedAtIsNull(Long userId);
}
