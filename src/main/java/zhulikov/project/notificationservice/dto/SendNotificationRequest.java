package zhulikov.project.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import zhulikov.project.notificationservice.enums.NotificationType;
import zhulikov.project.notificationservice.enums.PriorityType;

import java.util.Map;

@Data
@NoArgsConstructor
public class SendNotificationRequest {

    private NotificationType notificationType;
    private String destination;
    private String theme;
    private String content;
    private PriorityType priorityType =  PriorityType.MEDIUM;

    private Map<String,Object> variables;
    private Long templateId;
}
