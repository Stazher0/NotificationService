package zhulikov.project.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zhulikov.project.notificationservice.dto.TemplateCreateRequest;
import zhulikov.project.notificationservice.dto.TemplateResponse;
import zhulikov.project.notificationservice.entity.Template;
import zhulikov.project.notificationservice.exceptions.TemplateAlreadyExistsException;
import zhulikov.project.notificationservice.repository.TemplateRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final TemplateRepo templateRepo;

    public TemplateResponse create(TemplateCreateRequest dto) {

        if (templateRepo.existsByName(dto.getName())){
            throw new TemplateAlreadyExistsException(dto.getName());
        }

        Template template = createDtoToTemplate(dto);
        Template saved = templateRepo.save(template);

        log.info("Template saved id = {}",saved.getTemplateId());

        return templateToResponseDto(saved);
    }

    public TemplateResponse update(Long id,TemplateCreateRequest dto) {

        Template template = templateRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template " + id + " not found"));

        if (!dto.getName().equals(template.getName())
                && templateRepo.existsByName(dto.getName())) {
            throw new TemplateAlreadyExistsException(dto.getName());
        }

        applyDtoToTemplate(template, dto);

        Template saved = templateRepo.save(template);

        log.info("Template updated id = {}",saved.getTemplateId());

        return templateToResponseDto(saved);
    }

    public void delete(Long id) {

        if (!templateRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template " + id + " not found");
        }

        templateRepo.deleteById(id);

        log.info("Template deleted id = {}",id);
    }

    private Template createDtoToTemplate(TemplateCreateRequest dto) {

        Template template = new Template();

        template.setName(dto.getName());
        template.setTheme(dto.getTheme());
        template.setContent(dto.getContent());
        template.setNotificationType(dto.getNotificationType());

        return template;
    }

    private TemplateResponse templateToResponseDto(Template template) {

        TemplateResponse templateResponse = new TemplateResponse();

        templateResponse.setTemplateId(template.getTemplateId());
        templateResponse.setName(template.getName());
        templateResponse.setTheme(template.getTheme());
        templateResponse.setContent(template.getContent());
        templateResponse.setNotificationType(template.getNotificationType());
        templateResponse.setCreatedAt(template.getCreatedAt());

        return templateResponse;
    }

    private void applyDtoToTemplate(Template template, TemplateCreateRequest dto) {

        template.setNotificationType(dto.getNotificationType());
        template.setName(dto.getName());
        template.setTheme(dto.getTheme());
        template.setContent(dto.getContent());
    }
}
