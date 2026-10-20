package zhulikov.project.notificationservice.mapper;

import org.junit.jupiter.api.Test;
import zhulikov.project.notificationservice.dto.EmailNotificationDto;
import zhulikov.project.notificationservice.dto.PushNotificationDto;
import zhulikov.project.notificationservice.dto.SendNotificationRequest;
import zhulikov.project.notificationservice.dto.SmsNotificationDto;
import zhulikov.project.notificationservice.entity.Notification;
import zhulikov.project.notificationservice.enums.NotificationType;
import zhulikov.project.notificationservice.enums.PriorityType;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationMapperTest {

    private final NotificationMapper mapper =  new NotificationMapper();

    //Tests
    @Test
    void toNotification_mapAllFields(){
        SendNotificationRequest request = givenSendRequestWithoutTemplate();

        Notification notification = mapper.toNotification(request);

        assertEquals("content", notification.getContent());
        assertEquals("destination", notification.getDestination());
        assertEquals(NotificationType.PUSH, notification.getNotificationType());
        assertEquals(PriorityType.MEDIUM, notification.getPriorityType());
    }
    @Test
    void toNotification_defaultPriority(){
        SendNotificationRequest request = givenSendRequestWithoutTemplateAndPriority();

        Notification notification = mapper.toNotification(request);

        assertEquals("content", notification.getContent());
        assertEquals("destination", notification.getDestination());
        assertEquals(NotificationType.PUSH, notification.getNotificationType());
        assertEquals(PriorityType.MEDIUM, notification.getPriorityType());
    }
    @Test
    void toSmsDto_mapAllFields(){
        Notification notification = givenNotification(NotificationType.SMS);

        SmsNotificationDto dto = mapper.toSmsDto(notification);

        assertEquals(1L, dto.getNotificationId());
        assertEquals("content", dto.getContent());
        assertEquals("destination", dto.getDestination());
    }
    @Test
    void toEmailDto_mapAllFields(){
        Notification notification = givenNotification(NotificationType.EMAIL);

        EmailNotificationDto dto = mapper.toEmailDto(notification);

        assertEquals(1L, dto.getNotificationId());
        assertEquals("content", dto.getContent());
        assertEquals("destination", dto.getDestination());
        assertEquals("theme",dto.getTheme());
        assertEquals(PriorityType.MEDIUM, dto.getPriorityType());
    }
    @Test
    void toPushDto_mapAllFields(){
        Notification notification = givenNotification(NotificationType.PUSH);

        PushNotificationDto dto = mapper.toPushDto(notification);

        assertEquals(1L, dto.getNotificationId());
        assertEquals("content", dto.getContent());
        assertEquals("destination", dto.getDestination());
        assertEquals("theme",dto.getTheme());
    }

    //Helpers
    private Notification givenNotification(NotificationType notificationType){
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setContent("content");
        notification.setDestination("destination");
        notification.setNotificationType(notificationType);
        notification.setTheme("theme");
        notification.setPriorityType(PriorityType.MEDIUM);

        return notification;
    }
    private SendNotificationRequest givenSendRequestWithoutTemplate(){
        SendNotificationRequest request = new SendNotificationRequest();
        request.setContent("content");
        request.setDestination("destination");
        request.setNotificationType(NotificationType.PUSH);
        request.setPriorityType(PriorityType.MEDIUM);

        return request;
    }
    private SendNotificationRequest givenSendRequestWithoutTemplateAndPriority(){
        SendNotificationRequest request = new SendNotificationRequest();
        request.setContent("content");
        request.setDestination("destination");
        request.setNotificationType(NotificationType.PUSH);

        return request;
    }
}
