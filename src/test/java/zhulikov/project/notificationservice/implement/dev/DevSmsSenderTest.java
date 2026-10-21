package zhulikov.project.notificationservice.implement.dev;

import org.junit.jupiter.api.Test;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DevSmsSenderTest {

    private final DevSmsSender devSmsSender = new DevSmsSender();

    //Send Tests
    @Test
    void send_doesNotThrow(){
        SmsNotificationDto dto = givenSmsDto();

        assertDoesNotThrow(() -> devSmsSender.send(dto));
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
