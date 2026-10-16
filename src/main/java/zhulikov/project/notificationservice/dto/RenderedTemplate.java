package zhulikov.project.notificationservice.dto;

import zhulikov.project.notificationservice.enums.NotificationType;

public record RenderedTemplate(
    String content,
    String theme,
    NotificationType type
) {}
