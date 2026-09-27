package zhulikov.project.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zhulikov.project.notificationservice.dto.*;
import zhulikov.project.notificationservice.entity.Notification;
import zhulikov.project.notificationservice.producer.EmailProducer;
import zhulikov.project.notificationservice.producer.PushProducer;
import zhulikov.project.notificationservice.producer.SmsProducer;
import zhulikov.project.notificationservice.repository.NotificationRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final SmsProducer smsProducer;
    private final PushProducer pushProducer;
    private final EmailProducer emailProducer;

    public Long send (SendNotificationRequest request) {

        //выполняю маппинг
        Notification notification = toNotification(request);

        //сохраняю уведомление в бд
        Notification saved = notificationRepo.save(notification);

        //в зависимости от типа notification вызываю должный producer
        switch (request.getNotificationType()) {
            case SMS -> smsProducer.send(toSmsDto(saved));
            case EMAIL -> emailProducer.send(toEmailDto(saved));
            case PUSH -> pushProducer.send(toPushDto(saved));
            default -> throw new IllegalArgumentException(
                    "Unknown notification type: " + request.getNotificationType()
            );
        }

        log.info("Notification saved, id=({})", saved.getId());
        return  saved.getId();
    }

    private Notification toNotification (SendNotificationRequest request) {
        Notification notification = new Notification();
        notification.setDestination(request.getDestination());
        notification.setTheme(request.getTheme());
        notification.setContent(request.getContent());
        notification.setNotificationType(request.getNotificationType());

        if (request.getPriorityType() != null) {
            notification.setPriorityType(request.getPriorityType());
        }

        log.info("priorityType from request: {}", request.getPriorityType());

        return notification;
    }

    private SmsNotificationDto toSmsDto (Notification notification) {
        SmsNotificationDto dto = new SmsNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setNotificationId(notification.getId());

        return dto;
    }

    private EmailNotificationDto toEmailDto (Notification notification) {
        EmailNotificationDto dto = new EmailNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setPriorityType(notification.getPriorityType());
        dto.setTheme(notification.getTheme());
        dto.setNotificationId(notification.getId());

        return dto;
    }

    private PushNotificationDto toPushDto (Notification notification) {
        PushNotificationDto dto = new PushNotificationDto();

        dto.setContent(notification.getContent());
        dto.setDestination(notification.getDestination());
        dto.setTheme(notification.getTheme());
        dto.setNotificationId(notification.getId());

        return dto;
    }
}