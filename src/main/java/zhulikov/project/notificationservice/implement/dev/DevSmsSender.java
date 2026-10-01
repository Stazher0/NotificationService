package zhulikov.project.notificationservice.implement.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.service.SmsSender;

@Service
@Profile("dev")
@Slf4j
public class DevSmsSender implements SmsSender {

    @Override
    public void send(SmsNotificationDto dto){
        log.info("SMS SENDING to: {}", dto.getDestination());
        log.info("Sms sent: id={}", dto.getNotificationId());
    }
}
