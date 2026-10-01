package zhulikov.project.notificationservice.service;

import zhulikov.project.notificationservice.dto.PushNotificationDto;

public interface PushSender {
    void send(PushNotificationDto dto);
}
