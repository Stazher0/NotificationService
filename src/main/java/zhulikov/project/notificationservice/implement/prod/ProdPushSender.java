package zhulikov.project.notificationservice.implement.prod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.service.PushSender;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class ProdPushSender implements PushSender {

    private final RestTemplate restTemplate;

    @Value("${telegram.bot-token}")
    private String token;
    @Value("${telegram.chat-id}")
    private String chatId;

    @Override
    public void send(PushNotificationDto dto) {
        log.info("PUSH SENDING");

        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(createBody(dto), createHeaders());

        restTemplate.postForEntity(url, request, String.class);

        log.info("Push sent: id={}", dto.getNotificationId());
    }

    private Map<String, Object> createBody(PushNotificationDto dto) {
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", dto.getTheme() + "\n" + dto.getContent());

        return body;
    }

    private HttpHeaders createHeaders(){
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        return header;
    }
}
