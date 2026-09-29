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
import zhulikov.project.notificationservice.mapper.TemplateMapper;
import zhulikov.project.notificationservice.repository.TemplateRepo;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final TemplateRepo templateRepo;
    private final TemplateMapper templateMapper;

    private static final Pattern VARIABLE =
            Pattern.compile("\\{\\{\\s*([^{}]+?)\\s*}}");

    public String render(Map<String,Object> variables, Long templateId) {

        Template template = templateRepo.findById(templateId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        String result = replaceVariables(template.getContent(), variables);

        log.debug("Rendered template id={} with {} variables", templateId, variables.size());

        return result;
    }

    public TemplateResponse create(TemplateCreateRequest dto) {

        if (templateRepo.existsByName(dto.getName())){
            throw new TemplateAlreadyExistsException(dto.getName());
        }

        Template template = templateMapper.createDtoToTemplate(dto);
        Template saved = templateRepo.save(template);

        log.info("Template saved id = {}",saved.getTemplateId());

        return templateMapper.templateToResponseDto(saved);
    }

    public TemplateResponse update(Long id,TemplateCreateRequest dto) {

        Template template = templateRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template " + id + " not found"));

        if (!dto.getName().equals(template.getName())
                && templateRepo.existsByName(dto.getName())) {
            throw new TemplateAlreadyExistsException(dto.getName());
        }

        templateMapper.applyDtoToTemplate(template, dto);

        Template saved = templateRepo.save(template);

        log.info("Template updated id = {}",saved.getTemplateId());

        return templateMapper.templateToResponseDto(saved);
    }

    public void delete(Long id) {

        if (!templateRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template " + id + " not found");
        }

        templateRepo.deleteById(id);

        log.info("Template deleted id = {}",id);
    }

    private String replaceVariables(String content, Map<String,Object> variables) {

        Matcher matcher = VARIABLE.matcher(content);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = variables.get(key);

            String replacement = value == null
                    ? matcher.group(0)       // оставить {{key}}, если значения нет
                    : String.valueOf(value);

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }

}
