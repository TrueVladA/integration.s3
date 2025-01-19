package ru.bpmcons.operation.preview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bpmcons.preview.base.PreviewGenerationService;
import ru.bpmcons.preview.base.domain.PreviewFile;
import ru.bpmcons.preview.base.dto.GeneratePreviewRequest;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/files/{id}")
@RequiredArgsConstructor
public class PreviewController {
    private final PreviewGenerationService service;

    @PostMapping("/generate-preview")
    public PreviewFile presign(@Valid @RequestBody GeneratePreviewRequest dto, @PathVariable("id") String id) {
        return service.uploadPreview(id, dto);
    }
}
