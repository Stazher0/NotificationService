package zhulikov.project.notificationservice.implement.prod;

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
import org.springframework.web.client.RestTemplate;
import zhulikov.project.notificationservice.dto.PushNotificationDto;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProdPushSenderTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ProdPushSender prodPushSender;
    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(prodPushSender,"token","test-token");
        ReflectionTestUtils.setField(prodPushSender,"chatId","123456789");
    }

    //Tests
    @Test
    void send_callTelegram(){
        PushNotificationDto dto = givenPushDto();
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        prodPushSender.send(dto);

        verify(restTemplate).postForEntity(urlCaptor.capture(), entityCaptor.capture(), eq(String.class));

        assertEquals("https://api.telegram.org/bottest-token/sendMessage", urlCaptor.getValue());

        HttpEntity<?> entity = entityCaptor.getValue();
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());

        Map<String, Object> body = (Map<String, Object>) entity.getBody();
        assertEquals("123456789", body.get("chat_id"));
        assertEquals("Theme\nTest PUSH", body.get("text"));
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
