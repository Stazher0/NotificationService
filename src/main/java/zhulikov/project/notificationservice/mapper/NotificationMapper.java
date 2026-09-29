package zhulikov.project.notificationservice.mapper;

import org.springframework.stereotype.Component;
import zhulikov.project.notificationservice.dto.EmailNotificationDto;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.dto.SendNotificationRequest;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.entity.Notification;

@Component
public class NotificationMapper {

    public Notification toNotification (SendNotificationRequest request) {
        Notification notification = new Notification();
        notification.setDestination(request.getDestination());
        notification.setTheme(request.getTheme());
        notification.setContent(request.getContent());
        notification.setNotificationType(request.getNotificationType());

        if (request.getPriorityType() != null) {
            notification.setPriorityType(request.getPriorityType());
        }

        return notification;
    }

    public SmsNotificationDto toSmsDto (Notification notification) {
        SmsNotificationDto dto = new SmsNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setNotificationId(notification.getId());

        return dto;
    }

    public EmailNotificationDto toEmailDto (Notification notification) {
        EmailNotificationDto dto = new EmailNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setPriorityType(notification.getPriorityType());
        dto.setTheme(notification.getTheme());
        dto.setNotificationId(notification.getId());

        return dto;
    }

    public PushNotificationDto toPushDto (Notification notification) {
        PushNotificationDto dto = new PushNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setTheme(notification.getTheme());
        dto.setNotificationId(notification.getId());

        return dto;
    }
}
