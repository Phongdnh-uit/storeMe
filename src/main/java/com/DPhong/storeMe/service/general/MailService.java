package com.DPhong.storeMe.service.general;

import java.util.Map;

public interface MailService {

  void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

  void sendEmailFromTemplate(
      String to, String subject, String templateName, Map<String, Object> model);

  void sendActivationEmail(String to, Long userId, String code);
}
