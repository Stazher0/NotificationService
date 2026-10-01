package zhulikov.project.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.service.NotificationStatusService;
import zhulikov.project.notificationservice.service.PushSender;

import static zhulikov.project.notificationservice.config.RabbitNames.PUSH_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
//КЛАСС ЗАБИРАЕТ message из queue
public class PushConsumer {

    private final NotificationStatusService notificationStatusService;
    private final PushSender pushSender;

    @RabbitListener(queues = PUSH_QUEUE,messageConverter = "messageConverter",concurrency = "3")
    public void receive(PushNotificationDto dto) {
        Long notificationId = dto.getNotificationId();
        try {
            log.info("Push received {}",dto);
            pushSender.send(dto);
            notificationStatusService.markAsSent(notificationId);
        }catch (Exception error){
            notificationStatusService.markAsFailed(notificationId,error);
            throw new RuntimeException("Failed to process push: " + notificationId, error);
        }
    }
}
