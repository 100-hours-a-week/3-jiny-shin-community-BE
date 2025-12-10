package com.jinyshin.ktbcommunity.global.service;

import com.jinyshin.ktbcommunity.domain.feedback.entity.Feedback;
import com.jinyshin.ktbcommunity.global.config.SesProperties;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

@Service
@Slf4j
public class EmailService {

  private final SesClient sesClient;
  private final SesProperties sesProperties;

  @Autowired
  public EmailService(@Autowired(required = false) SesClient sesClient, SesProperties sesProperties) {
    this.sesClient = sesClient;
    this.sesProperties = sesProperties;
  }

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  public void sendFeedbackNotification(Feedback feedback) {
    if (!sesProperties.isEnabled() || sesClient == null) {
      log.info("[EmailService] 이메일 발송이 비활성화되어 있습니다. 피드백 ID: {}", feedback.getId());
      return;
    }

    String subject = "새 피드백 도착";
    String htmlBody = buildFeedbackEmailHtml(feedback);
    String textBody = buildFeedbackEmailText(feedback);

    try {
      SendEmailRequest request = SendEmailRequest.builder()
          .source(sesProperties.getFromEmail())
          .destination(Destination.builder()
              .toAddresses(sesProperties.getToEmail())
              .build())
          .message(Message.builder()
              .subject(Content.builder()
                  .charset("UTF-8")
                  .data(subject)
                  .build())
              .body(Body.builder()
                  .html(Content.builder()
                      .charset("UTF-8")
                      .data(htmlBody)
                      .build())
                  .text(Content.builder()
                      .charset("UTF-8")
                      .data(textBody)
                      .build())
                  .build())
              .build())
          .build();

      sesClient.sendEmail(request);
      log.info("[EmailService] 피드백 알림 이메일 발송 성공. 피드백 ID: {}", feedback.getId());

    } catch (SesException e) {
      log.error("[EmailService] 피드백 알림 이메일 발송 실패. 피드백 ID: {}, 에러: {}",
          feedback.getId(), e.awsErrorDetails().errorMessage());
    } catch (Exception e) {
      log.error("[EmailService] 피드백 알림 이메일 발송 중 예외 발생. 피드백 ID: {}, 에러: {}",
          feedback.getId(), e.getMessage());
    }
  }

  private String buildFeedbackEmailHtml(Feedback feedback) {
    String platform = feedback.getPlatform() != null ? feedback.getPlatform() : "N/A";
    String appVersion = feedback.getAppVersion() != null ? feedback.getAppVersion() : "N/A";
    String createdAt = feedback.getCreatedAt().format(FORMATTER);

    return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
        </head>
        <body style="margin:0; padding:20px; background-color:#f5f5f5; font-family:Arial, sans-serif;">
          <div style="max-width:600px; margin:0 auto; background-color:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
            <div style="background-color:#4F46E5; padding:20px; text-align:center;">
              <h1 style="color:#ffffff; margin:0; font-size:24px;">📬 새 피드백 도착</h1>
            </div>
            <div style="padding:30px;">
              <div style="background-color:#f8f9fa; border-left:4px solid #4F46E5; padding:15px; margin-bottom:20px;">
                <p style="margin:0; color:#333; line-height:1.6; white-space:pre-wrap;">%s</p>
              </div>
              <table style="width:100%%; border-collapse:collapse;">
                <tr>
                  <td style="padding:10px 0; border-bottom:1px solid #eee; color:#666; width:100px;">플랫폼</td>
                  <td style="padding:10px 0; border-bottom:1px solid #eee; color:#333;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px 0; border-bottom:1px solid #eee; color:#666;">앱 버전</td>
                  <td style="padding:10px 0; border-bottom:1px solid #eee; color:#333;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px 0; color:#666;">생성 시간</td>
                  <td style="padding:10px 0; color:#333;">%s</td>
                </tr>
              </table>
            </div>
            <div style="background-color:#f8f9fa; padding:15px; text-align:center; color:#999; font-size:12px;">
              KTB Community 피드백 알림 시스템
            </div>
          </div>
        </body>
        </html>
        """,
        escapeHtml(feedback.getContent()),
        escapeHtml(platform),
        escapeHtml(appVersion),
        createdAt);
  }

  private String escapeHtml(String text) {
    if (text == null) {
      return "";
    }
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }

  private String buildFeedbackEmailText(Feedback feedback) {
    return String.format("""
            새 피드백 도착

            - 내용: %s
            - 플랫폼: %s
            - 앱 버전: %s
            - 생성 시간: %s
            """,
        feedback.getContent(),
        feedback.getPlatform() != null ? feedback.getPlatform() : "N/A",
        feedback.getAppVersion() != null ? feedback.getAppVersion() : "N/A",
        feedback.getCreatedAt().format(FORMATTER));
  }
}