package com.jinyshin.ktbcommunity.domain.user.repository;

import com.jinyshin.ktbcommunity.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmailAndDeletedAtIsNull(String email);

  boolean existsByNicknameAndDeletedAtIsNull(String nickname);
  
  @EntityGraph(attributePaths = {"profileImage"})
  Optional<User> findByUserIdAndDeletedAtIsNull(Long userId);

  Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
