package com.jinyshin.ktbcommunity.domain.post.dto.response;

import com.jinyshin.ktbcommunity.global.common.AuthorInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시글 상세 응답")
public record PostDetailResponse(
    @Schema(description = "게시글 ID", example = "1")
    Long postId,

    @Schema(description = "게시글 제목", example = "안녕하세요 첫 게시글입니다")
    String title,

    @Schema(description = "게시글 본문", example = "이것은 게시글의 본문 내용입니다.")
    String content,

    @Schema(description = "작성자 정보")
    AuthorInfo author,

    @Schema(description = "좋아요 수", example = "10")
    Integer likeCount,

    @Schema(description = "댓글 수", example = "5")
    Integer commentCount,

    @Schema(description = "조회 수", example = "100")
    Integer viewCount,

    @Schema(description = "생성 일시", example = "2025-01-01T00:00:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 일시", example = "2025-01-02T00:00:00")
    LocalDateTime updatedAt,

    @Schema(description = "사용자의 좋아요 여부 (로그인 시에만 제공)", example = "true")
    Boolean isLiked,

    @Schema(description = "사용자가 작성자인지 여부 (로그인 시에만 제공)", example = "false")
    Boolean isAuthor,

    @Schema(description = "게시글 이미지 목록")
    List<PostImageResponse> images
) {

}
