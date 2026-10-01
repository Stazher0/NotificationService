package zhulikov.project.notificationservice.implement.prod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.service.SmsSender;

@Service
@Slf4j
@Profile("prod")
public class ProdSmsSender implements SmsSender {

    @Override
    public void send(SmsNotificationDto dto){
        log.info("SMS SENDING to: {}", dto.getDestination());
        // здесь должен быть реальный HTTP вызов провайдера
        // RestTemplate.postForEntity(sms.provider-url + "/send",
        // Map.of("api_id", sms.api-key, "to", dto.getDestination(), "msg", dto.getContent()),String.class);
        log.info("Sms sent: id={}", dto.getNotificationId());
    }
}
