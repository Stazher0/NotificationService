package zhulikov.project.notificationservice.implement.prod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import zhulikov.project.notificationservice.dto.EmailNotificationDto;
import zhulikov.project.notificationservice.enums.PriorityType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProdEmailSenderTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private ProdEmailSender prodEmailSender;

    //Tests
    @Test
    void send_callJavaMailSender(){
        EmailNotificationDto dto = givenEmailDto();
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        prodEmailSender.send(dto);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("noreply@test.com", message.getFrom());
        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("Test Theme", message.getSubject());
        assertEquals("Test Content", message.getText());
    }

    //Helpers
    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(prodEmailSender,"fromValue","noreply@test.com");
    }
    private EmailNotificationDto givenEmailDto() {
        EmailNotificationDto dto = new EmailNotificationDto();
        dto.setNotificationId(1L);
        dto.setDestination("test@example.com");
        dto.setTheme("Test Theme");
        dto.setContent("Test Content");
        dto.setPriorityType(PriorityType.MEDIUM);
        return dto;
    }
}
