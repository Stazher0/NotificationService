package zhulikov.project.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zhulikov.project.notificationservice.enums.NotificationType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {

    // id шаблона
    private Long templateId;

    // название шаблона
    private String name;

    // тема шаблона
    private String theme;

    // тело шаблона
    private String content;

    private NotificationType notificationType;

    //дата создания шаблона
    private Date createdAt;
}
