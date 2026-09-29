package zhulikov.project.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zhulikov.project.notificationservice.dto.*;
import zhulikov.project.notificationservice.entity.Notification;
import zhulikov.project.notificationservice.mapper.NotificationMapper;
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
    private final NotificationMapper notificationMapper;

    public Long send (SendNotificationRequest request) {

        //выполняю маппинг
        Notification notification = notificationMapper.toNotification(request);

        //сохраняю уведомление в бд
        Notification saved = notificationRepo.save(notification);

        //в зависимости от типа notification вызываю должный producer
        switch (request.getNotificationType()) {
            case SMS -> smsProducer.send(notificationMapper.toSmsDto(saved));
            case EMAIL -> emailProducer.send(notificationMapper.toEmailDto(saved));
            case PUSH -> pushProducer.send(notificationMapper.toPushDto(saved));
            default -> throw new IllegalArgumentException(
                    "Unknown notification type: " + request.getNotificationType()
            );
        }

        log.info("priorityType from request: {}", request.getPriorityType());
        log.info("Notification saved, id=({})", saved.getId());
        return  saved.getId();
    }
}