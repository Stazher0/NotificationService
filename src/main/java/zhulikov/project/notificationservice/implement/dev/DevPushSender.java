package zhulikov.project.notificationservice.implement.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.service.PushSender;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

@Service
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
public class DevPushSender implements PushSender {

    private final RestTemplate restTemplate;

    @Value("${gotify.url}")
    private String gotifyUrl;
    @Value("${gotify.token}")
    private String gotifyToken;


    @Override
    public void send(PushNotificationDto dto) {
        log.info("PUSH SENDING to: {}", dto.getDestination());

        String url = gotifyUrl + "/message";

        //header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-Gotify-Key", gotifyToken);

        //Тело
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("title", dto.getTheme());
        form.add("message", dto.getContent());
        form.add("priority", "3");

        //Оборачиваем body(form) и header в формат требуемый gotify
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        restTemplate.postForEntity(url, request, String.class);

        log.info("Push sent: id={}", dto.getNotificationId());
    }
}
