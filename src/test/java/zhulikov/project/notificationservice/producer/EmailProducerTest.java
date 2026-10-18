package zhulikov.project.notificationservice.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import zhulikov.project.notificationservice.config.RabbitNames;
import zhulikov.project.notificationservice.dto.EmailNotificationDto;
import zhulikov.project.notificationservice.enums.PriorityType;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static zhulikov.project.notificationservice.config.RabbitNames.NOTIFICATION_EXCHANGE;

@ExtendWith(MockitoExtension.class)
public class EmailProducerTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private EmailProducer emailProducer;

    //Send Tests
    @Test
    void send_highPriority_useHighRoutingKey(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.HIGH);

        emailProducer.send(dto);

        verify(rabbitTemplate).convertAndSend(
                eq(NOTIFICATION_EXCHANGE),
                eq(RabbitNames.ROUTING_EMAIL_HIGH),
                eq(dto)
        );
    }
    @Test
    void send_mediumPriority_useMediumRoutingKey(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.MEDIUM);

        emailProducer.send(dto);

        verify(rabbitTemplate).convertAndSend(
                eq(NOTIFICATION_EXCHANGE),
                eq(RabbitNames.ROUTING_EMAIL_MEDIUM),
                eq(dto)
        );
    }
    @Test
    void send_lowPriority_useLowRoutingKey(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.LOW);

        emailProducer.send(dto);

        verify(rabbitTemplate).convertAndSend(
                eq(NOTIFICATION_EXCHANGE),
                eq(RabbitNames.ROUTING_EMAIL_LOW),
                eq(dto)
        );
    }
    @Test
    void send_nullPriority_throwException(){
        EmailNotificationDto dto = givenEmailDto(null);

        assertThrows(IllegalArgumentException.class,() -> emailProducer.send(dto));
        verifyNoInteractions(rabbitTemplate);
    }

    //Helpers
    private EmailNotificationDto givenEmailDto(PriorityType priorityType) {
        EmailNotificationDto dto = new EmailNotificationDto();
        dto.setNotificationId(1L);
        dto.setDestination("test@example.com");
        dto.setTheme("Test Theme");
        dto.setContent("Test Content");
        dto.setPriorityType(priorityType);
        return dto;
    }
}
