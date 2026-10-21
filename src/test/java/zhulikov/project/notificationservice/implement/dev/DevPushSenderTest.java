package zhulikov.project.notificationservice.implement.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import zhulikov.project.notificationservice.dto.PushNotificationDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DevPushSenderTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private DevPushSender devPushSender;
    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(devPushSender,"gotifyUrl","http://gotify:80");
        ReflectionTestUtils.setField(devPushSender,"gotifyToken","test-token");
    }

    //Tests
    @Test
    void send_callGotify(){
        PushNotificationDto dto = givenPushDto();
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        devPushSender.send(dto);

        verify(restTemplate).postForEntity(urlCaptor.capture(),entityCaptor.capture(),eq(String.class));

        assertEquals("http://gotify:80/message",urlCaptor.getValue());

        HttpEntity entity = entityCaptor.getValue();
        assertEquals("test-token", entity.getHeaders().getFirst("X-Gotify-Key"));
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, entity.getHeaders().getContentType());

        MultiValueMap<String, String> body = (MultiValueMap<String, String>) entity.getBody();
        assertEquals("Theme", body.getFirst("title"));
        assertEquals("Test PUSH", body.getFirst("message"));
        assertEquals("3", body.getFirst("priority"));
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
