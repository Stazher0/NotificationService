package zhulikov.project.notificationservice.mapper;

import org.junit.jupiter.api.Test;
import zhulikov.project.notificationservice.dto.TemplateCreateRequest;
import zhulikov.project.notificationservice.dto.TemplateResponse;
import zhulikov.project.notificationservice.entity.Template;
import zhulikov.project.notificationservice.enums.NotificationType;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TemplateMapperTest {

    private final TemplateMapper mapper = new TemplateMapper();

    //Tests
    @Test
    void applyDtoToTemplate_overwriteFields() {
        Template template = givenTemplate();
        TemplateCreateRequest dto = givenTemplateCreateRequest();
        dto.setName("new name");
        dto.setContent("new content");
        dto.setTheme("new theme");
        dto.setNotificationType(NotificationType.SMS);

        mapper.applyDtoToTemplate(template, dto);

        assertEquals("new name", template.getName());
        assertEquals("new content", template.getContent());
        assertEquals("new theme", template.getTheme());
        assertEquals(NotificationType.SMS, template.getNotificationType());
        assertEquals(1L, template.getTemplateId());
    }
    @Test
    void createDtoToTemplate_mapAllFields(){
        TemplateCreateRequest dto = givenTemplateCreateRequest();

        Template template = mapper.createDtoToTemplate(dto);

        assertEquals("name", template.getName());
        assertEquals("content", template.getContent());
        assertEquals("theme", template.getTheme());
        assertEquals(NotificationType.EMAIL, template.getNotificationType());
        assertNull(template.getTemplateId());
    }
    @Test
    void templateToResponseDto_mapAllFields(){
        Template template = givenTemplate();

        TemplateResponse response = mapper.templateToResponseDto(template);

        assertEquals(1L, response.getTemplateId());
        assertEquals("name", response.getName());
        assertEquals("theme", response.getTheme());
        assertEquals("content", response.getContent());
        assertEquals(template.getCreatedAt(), response.getCreatedAt());
    }

    //Helpers
    private Template givenTemplate(){
        Template template = new Template();
        template.setTemplateId(1L);
        template.setContent("content");
        template.setTheme("theme");
        template.setNotificationType(NotificationType.EMAIL);
        template.setName("name");
        template.setCreatedAt(new Date());

        return template;
    }
    private TemplateCreateRequest givenTemplateCreateRequest() {
        TemplateCreateRequest dto = new TemplateCreateRequest();
        dto.setName("name");
        dto.setContent("content");
        dto.setTheme("theme");
        dto.setNotificationType(NotificationType.EMAIL);
        return dto;
    }
}
