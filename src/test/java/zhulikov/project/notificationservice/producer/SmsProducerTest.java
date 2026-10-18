package zhulikov.project.notificationservice.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import zhulikov.project.notificationservice.config.RabbitNames;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import static org.mockito.Mockito.*;
import static zhulikov.project.notificationservice.config.RabbitNames.NOTIFICATION_EXCHANGE;

@ExtendWith(MockitoExtension.class)
public class SmsProducerTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private SmsProducer smsProducer;

    //Send tests
    @Test
    void send_useSmsRoutingKey(){
        SmsNotificationDto dto = givenSmsDto();

        smsProducer.send(dto);

        verify(rabbitTemplate).convertAndSend(
                eq(NOTIFICATION_EXCHANGE),
                eq(RabbitNames.ROUTING_SMS),
                eq(dto)
        );
    }

    //Helpers
    private SmsNotificationDto givenSmsDto() {
        SmsNotificationDto dto = new SmsNotificationDto();
        dto.setNotificationId(1L);
        dto.setDestination("+79991234567");
        dto.setContent("Test SMS");
        return dto;
    }
}
