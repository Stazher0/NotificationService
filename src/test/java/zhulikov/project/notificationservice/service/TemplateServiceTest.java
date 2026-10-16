package zhulikov.project.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.web.server.ResponseStatusException;
import zhulikov.project.notificationservice.dto.TemplateCreateRequest;
import zhulikov.project.notificationservice.dto.TemplateResponse;
import zhulikov.project.notificationservice.entity.Template;
import zhulikov.project.notificationservice.enums.NotificationType;
import zhulikov.project.notificationservice.exceptions.TemplateAlreadyExistsException;
import zhulikov.project.notificationservice.mapper.TemplateMapper;
import zhulikov.project.notificationservice.repository.TemplateRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepo templateRepo;
    @Mock
    private TemplateMapper templateMapper;
    @InjectMocks
    private TemplateService templateService;
    private final Long templateId=1L;

    //Delete Tests
    @Test
    void delete_ThrowWhenNotFound(){
        when(templateRepo.existsById(templateId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> templateService.delete(templateId));
        verify(templateRepo,never()).deleteById(templateId);
    }
    @Test
    void delete_CallDeleteById(){
        when(templateRepo.existsById(templateId)).thenReturn(true);

        templateService.delete(templateId);

        verify(templateRepo).deleteById(templateId);
    }

    //Update Tests
    @Test
    void update_skipNameCheckWhenSame(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("name");
        TemplateResponse response = givenTemplateResponse();
        Template existing = givenTemplateEntity();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existing));
        when(templateRepo.save(existing)).thenReturn(existing);
        when(templateMapper.templateToResponseDto(existing)).thenReturn(response);

        var result = templateService.update(templateId, dto);

        verify(templateRepo,never()).existsByName(any());
        verify(templateRepo).save(existing);
        assertEquals(response,result);
    }
    @Test
    void update_throwWhenNameTaken(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("test");
        Template existing = givenTemplateEntity();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existing));
        when(templateRepo.existsByName("test")).thenReturn(true);

        assertThrows(TemplateAlreadyExistsException.class, () -> templateService.update(templateId, dto));
        verify(templateRepo, never()).save(any());
    }
    @Test
    void update_throwWhenNotFound(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("test");

        when(templateRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> templateService.update(999L,dto));
        verify(templateRepo, never()).save(any());
    }
    @Test
    void update_saveTemplate(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("test");
        TemplateResponse response = givenTemplateResponse();
        Template existing = givenTemplateEntity();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existing));
        when(templateRepo.existsByName("test")).thenReturn(false);
        when(templateRepo.save(existing)).thenReturn(existing);
        when(templateMapper.templateToResponseDto(existing)).thenReturn(response);

        var result = templateService.update(templateId, dto);

        verify(templateRepo).save(existing);
        verify(templateMapper).templateToResponseDto(existing);
        assertEquals(response,result);
    }

    //Create Tests
    @Test
    void create_throwWhenNameExists(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("test");

        when(templateRepo.existsByName(dto.getName())).thenReturn(true);


        assertThrows(TemplateAlreadyExistsException.class,
                () -> templateService.create(dto));
        verify(templateRepo, never()).save(any());
    }
    @Test
    void create_saveTemplate(){
        TemplateCreateRequest dto = givenTemplateCreateRequest("test");
        TemplateResponse response = givenTemplateResponse();
        Template template = givenTemplateEntity();

        when(templateRepo.existsByName(dto.getName())).thenReturn(false);
        when(templateMapper.createDtoToTemplate(dto)).thenReturn(template);
        when(templateRepo.save(template)).thenReturn(template);
        when(templateMapper.templateToResponseDto(template)).thenReturn(response);

        var result = templateService.create(dto);

        verify(templateRepo).save(template);
        assertThat(result).isEqualTo(response);
    }

    private TemplateResponse givenTemplateResponse(){
        TemplateResponse templateResponse = new TemplateResponse();
        templateResponse.setTemplateId(templateId);
        templateResponse.setName("welcome");
        templateResponse.setContent("Hello, {{name}}!");
        templateResponse.setTheme("Welcome");
        templateResponse.setNotificationType(NotificationType.EMAIL);
        return templateResponse;
    }
    private Template givenTemplateEntity(){
        Template template = new Template();
        template.setNotificationType(NotificationType.EMAIL);
        template.setTemplateId(templateId);
        template.setTheme("theme");
        template.setName("name");
        template.setContent("content");

        return template;
    }
    private TemplateCreateRequest givenTemplateCreateRequest(String name){
        TemplateCreateRequest dto = new TemplateCreateRequest();
        dto.setName(name);
        dto.setContent("content");
        dto.setNotificationType(NotificationType.EMAIL);
        dto.setTheme("theme");

        return dto;
    }

    //Render Tests
    @Test
    void render_replaceOneVariable(){
        givenTemplate("Hello, {{name}}!");

        var result = templateService.render(Map.of("name","Ivan"),
                templateId);

        assertEquals("Hello, Ivan!", result.content());
    }
    @Test
    void render_replaceTwoVariables(){
        givenTemplate("{{a}} and {{b}}");

        var result = templateService.render(Map.of("a","1","b","2"),templateId);

        assertEquals("1 and 2", result.content());
    }
    @Test
    void render_replaceVariableWithSpaces(){
        givenTemplate("{{ name }}");

        var result = templateService.render(Map.of("name","Ivan"),templateId);

        assertEquals("Ivan", result.content());
    }
    @Test
    void render_keepPlaceholderWhenVariableMissing(){
        givenTemplate("{{name}}");

        var result = templateService.render(Map.of("address","Ivan"),templateId);

        assertEquals("{{name}}", result.content());
    }
    @Test
    void render_handleNullVariables(){
        givenTemplate("Hello, {{name}}");

        var result = templateService.render(null,templateId);

        assertEquals("Hello, {{name}}", result.content());
    }
    @Test
    void render_returnTextWhenNoPlaceHolders(){
        givenTemplate("Plain text without placeholders");

        var result = templateService.render(Map.of("name","Ivan"),templateId);

        assertEquals("Plain text without placeholders", result.content());
    }
    @Test
    void render_escapeDollarInValue(){
        givenTemplate("Money: {{money}}");

        var result = templateService.render(Map.of("money","$100"),templateId);

        assertEquals("Money: $100", result.content());
    }
    @Test
    void render_throwWhenTemplateNotFound(){
        when(templateRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> templateService.render(Map.of("surname","Zolo"),999L));
    }
    @Test void render_returnThemeAndType(){
        givenTemplate("surname: {{surname}}");

        var result = templateService.render(Map.of("surname","Zolo"),templateId);

        assertEquals("surname: Zolo", result.content());
        assertEquals("email",result.theme());
        assertEquals(NotificationType.EMAIL,result.type());
    }

    private void givenTemplate(String content){
        Template template = new Template();
        template.setContent(content);
        template.setTheme("email");
        template.setNotificationType(NotificationType.EMAIL);

        when(templateRepo.findById(templateId)).
                thenReturn(Optional.of(template));
    }
}
