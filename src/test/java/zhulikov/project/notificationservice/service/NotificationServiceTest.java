package zhulikov.project.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zhulikov.project.notificationservice.dto.RenderedTemplate;
import zhulikov.project.notificationservice.dto.SendNotificationRequest;
import zhulikov.project.notificationservice.entity.Notification;
import zhulikov.project.notificationservice.enums.NotificationType;
import zhulikov.project.notificationservice.enums.PriorityType;
import zhulikov.project.notificationservice.mapper.NotificationMapper;
import zhulikov.project.notificationservice.producer.EmailProducer;
import zhulikov.project.notificationservice.producer.PushProducer;
import zhulikov.project.notificationservice.producer.SmsProducer;
import zhulikov.project.notificationservice.repository.NotificationRepo;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private SmsProducer smsProducer;
    @Mock
    private PushProducer pushProducer;
    @Mock
    private EmailProducer emailProducer;
    @Mock
    private NotificationMapper  notificationMapper;
    @Mock
    private TemplateService templateService;
    @InjectMocks
    private NotificationService notificationService;

    //Send Tests
    @Test
    void send_withoutTemplate_useContentFromRequest(){
        Notification notification = givenNotification(NotificationType.SMS);
        SendNotificationRequest request = givenSendRequestWithoutTemplate(NotificationType.SMS);
        notification.setId(1L);

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(notificationRepo.save(notification)).thenReturn(notification);

        Long result = notificationService.send(request);

        verify(templateService,never()).render(any(),any());
        verify(notificationRepo).save(notification);
        assertEquals(1L,result);
    }
    @Test
    void send_saveNotification(){
        Notification notification = givenNotification(NotificationType.EMAIL);
        SendNotificationRequest request = givenSendRequestWithoutTemplate(NotificationType.EMAIL);

        notification.setId(1L);

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(notificationRepo.save(notification)).thenReturn(notification);

        Long result = notificationService.send(request);

        verify(notificationRepo).save(notification);
        assertEquals(notification.getId(),result);
    }
    @Test
    void send_withTemplate_renderContent(){
        Notification notification = givenNotification(NotificationType.EMAIL);
        SendNotificationRequest request = givenSendRequestWithTemplate(NotificationType.EMAIL,"test");
        RenderedTemplate rendered = givenRenderedTemplate();
        notification.setId(1L);
        Map<String,Object> variables = Map.of("name", "Ivan");

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(templateService.render(variables, 1L)).thenReturn(rendered);
        when(notificationRepo.save(notification)).thenReturn(notification);

        notificationService.send(request);

        ArgumentCaptor<Notification> captor =  ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("rendered content",saved.getContent());
    }
    @Test
    void send_themeFromRequest_overridesTemplate(){
        Notification notification = givenNotification(NotificationType.EMAIL);
        SendNotificationRequest request = givenSendRequestWithTemplate(NotificationType.EMAIL, "test");
        RenderedTemplate rendered = givenRenderedTemplate();
        notification.setId(1L);
        Map<String,Object> variables = Map.of("name", "Ivan");

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(templateService.render(variables, 1L)).thenReturn(rendered);
        when(notificationRepo.save(notification)).thenReturn(notification);

        notificationService.send(request);

        ArgumentCaptor<Notification> captor =  ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("test",saved.getTheme());
    }

    //SendToProducer Test
    @Test
    void send_callEmailProducer(){
        Notification notification = givenNotification(NotificationType.EMAIL);
        SendNotificationRequest request = givenSendRequestWithoutTemplate(NotificationType.EMAIL);
        notification.setId(1L);

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(notificationRepo.save(notification)).thenReturn(notification);

        notificationService.send(request);

        verify(emailProducer).send(any());
        verify(pushProducer, never()).send(any());
        verify(smsProducer,never()).send(any());
    }
    @Test
    void send_callPushProducer(){
        Notification notification = givenNotification(NotificationType.PUSH);
        SendNotificationRequest request = givenSendRequestWithoutTemplate(NotificationType.PUSH);
        notification.setId(1L);

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(notificationRepo.save(notification)).thenReturn(notification);

        notificationService.send(request);

        verify(emailProducer, never()).send(any());
        verify(pushProducer).send(any());
        verify(smsProducer,never()).send(any());
    }
    @Test
    void send_callSmsProducer(){
        Notification notification = givenNotification(NotificationType.SMS);
        SendNotificationRequest request = givenSendRequestWithoutTemplate(NotificationType.SMS);
        notification.setId(1L);

        when(notificationMapper.toNotification(request)).thenReturn(notification);
        when(notificationRepo.save(notification)).thenReturn(notification);

        notificationService.send(request);

        verify(emailProducer, never()).send(any());
        verify(pushProducer, never()).send(any());
        verify(smsProducer).send(any());
    }

    //Helpers
    private SendNotificationRequest givenSendRequestWithoutTemplate(NotificationType notificationType){
        SendNotificationRequest request = new SendNotificationRequest();
        request.setContent("content");
        request.setDestination("destination");
        request.setNotificationType(notificationType);
        request.setPriorityType(PriorityType.MEDIUM);

        return request;
    }
    private SendNotificationRequest givenSendRequestWithTemplate(NotificationType notificationType, String theme){
        SendNotificationRequest request = new SendNotificationRequest();
        request.setTheme(theme);
        request.setContent("content");
        request.setDestination("destination");
        request.setNotificationType(notificationType);
        request.setPriorityType(PriorityType.MEDIUM);
        request.setTemplateId(1L);
        request.setVariables(Map.of("name", "Ivan"));

        return request;
    }
    private Notification givenNotification(NotificationType notificationType){
        Notification notification = new Notification();
        notification.setContent("content");
        notification.setDestination("destination");
        notification.setNotificationType(notificationType);
        notification.setPriorityType(PriorityType.MEDIUM);
        notification.setTheme("default");

        return notification;
    }
    private RenderedTemplate givenRenderedTemplate(){
        return new RenderedTemplate("rendered content","theme", NotificationType.EMAIL);
    }
}
