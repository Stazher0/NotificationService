package zhulikov.project.notificationservice.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zhulikov.project.notificationservice.dto.EmailNotificationDto;
import zhulikov.project.notificationservice.enums.PriorityType;
import zhulikov.project.notificationservice.service.EmailSender;
import zhulikov.project.notificationservice.service.NotificationStatusService;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailConsumerTest {

    @Mock
    private NotificationStatusService notificationStatusService;
    @Mock
    private EmailSender emailSender;
    @InjectMocks
    private EmailConsumer emailConsumer;

    //ProcessEmail Tests
    @Test
    void receiveHigh_success_callSenderAndMarkAsSent(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.HIGH);

        emailConsumer.receiveHigh(dto);

        verify(emailSender).send(dto);
        verify(notificationStatusService).markAsSent(1L);
    }
    @Test
    void receiveMedium_success_callSenderAndMarkAsSent(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.MEDIUM);

        emailConsumer.receiveMedium(dto);

        verify(emailSender).send(dto);
        verify(notificationStatusService).markAsSent(1L);
    }
    @Test
    void receiveLow_success_callSenderAndMarkAsSent(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.LOW);

        emailConsumer.receiveLow(dto);

        verify(emailSender).send(dto);
        verify(notificationStatusService).markAsSent(1L);
    }
    @Test
    void receiveHigh_error_callMarkAsFailedAndThrow(){
        EmailNotificationDto dto = givenEmailDto(PriorityType.HIGH);

        doThrow(new RuntimeException("SMTP error")).when(emailSender).send(dto);

        assertThrows(RuntimeException.class, () -> emailConsumer.receiveHigh(dto));

        verify(notificationStatusService).markAsFailed(eq(1L), any(Exception.class));
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
