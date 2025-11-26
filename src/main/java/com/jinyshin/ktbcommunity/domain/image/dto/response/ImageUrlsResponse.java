package com.jinyshin.ktbcommunity.domain.image.dto.response;

/**
 * 이미지 URL 정보를 담는 DTO
 *
 * @param jpgUrl  JPG 포맷 URL
 * @param webpUrl WebP 포맷 URL
 */
public record ImageUrlsResponse(
    String jpgUrl,
    String webpUrl
) {

}