package zhulikov.project.notificationservice.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.service.NotificationStatusService;
import zhulikov.project.notificationservice.service.SmsSender;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SmsConsumerTest {

    @Mock
    private NotificationStatusService notificationStatusService;
    @Mock
    private SmsSender smsSender;
    @InjectMocks
    private SmsConsumer smsConsumer;

    //ProcessSms Tests
    @Test
    void receive_success_callSenderAndMarkAsSent() {
        SmsNotificationDto dto = givenSmsDto();

        smsConsumer.receive(dto);

        verify(smsSender).send(dto);
        verify(notificationStatusService).markAsSent(1L);
    }
    @Test
    void receive_error_callMarkAsFailedAndThrow(){
        SmsNotificationDto dto = givenSmsDto();

        doThrow(new RuntimeException("SMS error")).when(smsSender).send(dto);

        assertThrows(RuntimeException.class, () -> smsConsumer.receive(dto));

        verify(notificationStatusService).markAsFailed(eq(1L), any(Exception.class));
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
