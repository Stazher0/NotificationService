package zhulikov.project.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.service.NotificationStatusService;
import zhulikov.project.notificationservice.service.SmsSender;

import static zhulikov.project.notificationservice.config.RabbitNames.SMS_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
//КЛАСС ЗАБИРАЕТ message из queue
public class SmsConsumer {

    private final NotificationStatusService notificationStatusService;
    private final SmsSender smsSender;

    @RabbitListener(queues = SMS_QUEUE,messageConverter = "messageConverter",concurrency = "3")
    public void receive(SmsNotificationDto dto) {
        Long notificationId = dto.getNotificationId();

        try {
            log.info("SMS received {}",dto);
            smsSender.send(dto);
            notificationStatusService.markAsSent(notificationId);
        } catch (Exception error) {
            notificationStatusService.markAsFailed(notificationId,error);
            throw new RuntimeException("Failed to process sms: " + notificationId, error);
        }

    }
}
