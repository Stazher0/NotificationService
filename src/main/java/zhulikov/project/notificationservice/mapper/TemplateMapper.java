package zhulikov.project.notificationservice.mapper;

import org.springframework.stereotype.Component;
import zhulikov.project.notificationservice.dto.TemplateCreateRequest;
import zhulikov.project.notificationservice.dto.TemplateResponse;
import zhulikov.project.notificationservice.entity.Template;

@Component
public class TemplateMapper {
    public Template createDtoToTemplate(TemplateCreateRequest dto) {

        Template template = new Template();

        template.setName(dto.getName());
        template.setTheme(dto.getTheme());
        template.setContent(dto.getContent());
        template.setNotificationType(dto.getNotificationType());

        return template;
    }

    public TemplateResponse templateToResponseDto(Template template) {

        TemplateResponse templateResponse = new TemplateResponse();

        templateResponse.setTemplateId(template.getTemplateId());
        templateResponse.setName(template.getName());
        templateResponse.setTheme(template.getTheme());
        templateResponse.setContent(template.getContent());
        templateResponse.setNotificationType(template.getNotificationType());
        templateResponse.setCreatedAt(template.getCreatedAt());

        return templateResponse;
    }

    public void applyDtoToTemplate(Template template, TemplateCreateRequest dto) {

        template.setNotificationType(dto.getNotificationType());
        template.setName(dto.getName());
        template.setTheme(dto.getTheme());
        template.setContent(dto.getContent());
    }
}
