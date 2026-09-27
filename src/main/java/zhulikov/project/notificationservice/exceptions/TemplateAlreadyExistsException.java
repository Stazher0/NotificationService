package zhulikov.project.notificationservice.exceptions;

import lombok.Getter;

@Getter
public class TemplateAlreadyExistsException extends RuntimeException {
    private final String templateName;

    public TemplateAlreadyExistsException(String templateName) {
        super(String.format("Template with name '%s' already exists", templateName));
        this.templateName = templateName;
    }

}
