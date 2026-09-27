package zhulikov.project.notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhulikov.project.notificationservice.dto.TemplateCreateRequest;
import zhulikov.project.notificationservice.dto.TemplateResponse;

import zhulikov.project.notificationservice.service.TemplateService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService  templateService;

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@RequestBody @Valid TemplateCreateRequest dto) {

        TemplateResponse templateResponse = templateService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateResponse);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> update(@RequestBody @Valid TemplateCreateRequest dto, @PathVariable Long id) {

        return ResponseEntity.ok(templateService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        templateService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
