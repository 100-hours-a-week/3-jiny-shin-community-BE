package com.jinyshin.ktbcommunity.global.interceptor;

import com.jinyshin.ktbcommunity.domain.post.repository.PostRepository;
import com.jinyshin.ktbcommunity.global.exception.ForbiddenException;
import com.jinyshin.ktbcommunity.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostOwnershipInterceptor implements HandlerInterceptor {

  private final PostRepository postRepository;

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler) {

    String requestURI = request.getRequestURI();
    String method = request.getMethod();
    log.info("[PostOwnershipInterceptor] 작성자 검증 시작: {} {}", method, requestURI);

    // GET 요청은 작성자 검증 불필요 (조회는 누구나 가능)
    if ("GET".equals(method)) {
      log.debug("[PostOwnershipInterceptor] GET 요청 - 검증 스킵: {}", requestURI);
      return true;
    }

    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      log.warn("[PostOwnershipInterceptor] userId is null - should not happen after filter");
      throw ForbiddenException.forbidden();
    }

    // PathVariable에서 postId 추출
    @SuppressWarnings("unchecked")
    Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

    String postIdStr = pathVariables.get("postId");
    if (postIdStr == null) {
      log.debug("[PostOwnershipInterceptor] postId not found in path variables - skip");
      return true;
    }

    Long postId = Long.parseLong(postIdStr);

    // 게시글 작성자 확인
    Long authorId = postRepository.findAuthorIdByPostId(postId)
        .orElseThrow(ResourceNotFoundException::post);

    if (!authorId.equals(userId)) {
      log.warn("[PostOwnershipInterceptor] 권한 없음: userId={}, postId={}, authorId={}",
          userId, postId, authorId);
      throw ForbiddenException.forbidden();
    }

    log.info("[PostOwnershipInterceptor] 작성자 검증 성공: userId={}, postId={}", userId, postId);
    return true;
  }
}