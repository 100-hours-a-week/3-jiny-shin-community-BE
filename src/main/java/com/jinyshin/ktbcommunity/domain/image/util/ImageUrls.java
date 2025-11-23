package com.jinyshin.ktbcommunity.domain.image.util;

/**
 * 이미지 URL 정보를 담는 DTO
 *
 * @param jpgUrl  JPG 포맷 URL
 * @param webpUrl WebP 포맷 URL
 */
public record ImageUrls(
    String jpgUrl,
    String webpUrl
) {

}