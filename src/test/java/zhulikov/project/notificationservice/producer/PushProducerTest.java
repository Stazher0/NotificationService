package zhulikov.project.notificationservice.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import zhulikov.project.notificationservice.config.RabbitNames;
import zhulikov.project.notificationservice.dto.PushNotificationDto;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static zhulikov.project.notificationservice.config.RabbitNames.NOTIFICATION_EXCHANGE;

@ExtendWith(MockitoExtension.class)
public class PushProducerTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private PushProducer pushProducer;

    //Send tests
    @Test
    void send_usePushRoutingKey(){
        PushNotificationDto dto = givenPushDto();

        pushProducer.send(dto);

        verify(rabbitTemplate).convertAndSend(
                eq(NOTIFICATION_EXCHANGE),
                eq(RabbitNames.ROUTING_PUSH),
                eq(dto)
        );
    }

    //Helpers
    private PushNotificationDto givenPushDto() {
        PushNotificationDto dto = new PushNotificationDto();
        dto.setNotificationId(1L);
        dto.setDestination("id/dasasd");
        dto.setContent("Test PUSH");
        dto.setTheme("Theme");
        return dto;
    }
}
