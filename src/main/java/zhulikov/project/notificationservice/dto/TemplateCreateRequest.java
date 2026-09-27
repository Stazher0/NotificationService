package zhulikov.project.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import zhulikov.project.notificationservice.enums.NotificationType;

@Data
@NoArgsConstructor
public class TemplateCreateRequest {

    // название шаблона
    @NotBlank
    private String name;

    // тема шаблона
    private String theme;

    // тело шаблона
    @NotBlank
    private String content;

    @NotNull
    private NotificationType notificationType;
}
