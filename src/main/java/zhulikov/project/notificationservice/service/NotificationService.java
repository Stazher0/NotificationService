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
    private final TemplateService templateService;

    public Long send (SendNotificationRequest request) {

        //выполняю маппинг
        Notification notification = notificationMapper.toNotification(request);

        if (request.getTemplateId() != null) {
            RenderedTemplate renderedTemplate = templateService.render(request.getVariables(), request.getTemplateId());
            notification.setContent(renderedTemplate.content());
            if (request.getTheme()==null) {
                notification.setTheme(renderedTemplate.theme());
            }
            notification.setTemplateId(request.getTemplateId());
        }

        //сохраняю уведомление в бд
        Notification saved = notificationRepo.save(notification);


        //в зависимости от типа notification вызываю подходящий producer
        sendToProducer(saved);

        log.info("priorityType from request: {}", request.getPriorityType());
        log.info("Notification saved, id=({})", saved.getId());
        return  saved.getId();
    }

    private void sendToProducer (Notification notification) {
        switch (notification.getNotificationType()) {
            case SMS -> smsProducer.send(notificationMapper.toSmsDto(notification));
            case EMAIL -> emailProducer.send(notificationMapper.toEmailDto(notification));
            case PUSH -> pushProducer.send(notificationMapper.toPushDto(notification));
            default -> throw new IllegalArgumentException(
                    "Unknown notification type: " + notification.getNotificationType()
            );
        }
    }
}