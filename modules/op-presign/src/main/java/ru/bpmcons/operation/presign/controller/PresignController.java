package ru.bpmcons.operation.presign.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bpmcons.operation.presign.PresignService;
import ru.bpmcons.operation.presign.dto.PresignRequest;
import ru.bpmcons.operation.presign.dto.PresignResponse;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/files/{id}")
@RequiredArgsConstructor
public class PresignController {
    private final PresignService presignService;

    @PostMapping("/generate-presign")
    public PresignResponse presign(@Valid @RequestBody PresignRequest dto, @PathVariable("id") String id) {
        return presignService.createPresign(id, dto);
    }
}
