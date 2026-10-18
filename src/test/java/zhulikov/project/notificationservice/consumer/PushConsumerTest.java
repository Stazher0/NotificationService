package zhulikov.project.notificationservice.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.service.NotificationStatusService;
import zhulikov.project.notificationservice.service.PushSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PushConsumerTest {

    @Mock
    private NotificationStatusService notificationStatusService;
    @Mock
    private PushSender pushSender;
    @InjectMocks
    private PushConsumer pushConsumer;

    //Receive Tests
    @Test
    void receive_success_callSenderAndMarkAsSent() {
        PushNotificationDto dto = givenPushDto();

        pushConsumer.receive(dto);

        verify(pushSender).send(dto);
        verify(notificationStatusService).markAsSent(1L);
    }
    @Test
    void receive_error_callMarkAsFailedAndThrow(){
        PushNotificationDto dto = givenPushDto();

        doThrow(new RuntimeException("Push error")).when(pushSender).send(dto);

        assertThrows(RuntimeException.class, () -> pushConsumer.receive(dto));

        verify(notificationStatusService).markAsFailed(eq(1L), any(Exception.class));
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
