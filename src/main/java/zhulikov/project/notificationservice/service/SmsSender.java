package zhulikov.project.notificationservice.service;

import zhulikov.project.notificationservice.dto.SmsNotificationDto;

public interface SmsSender {
    void send(SmsNotificationDto dto);
}
