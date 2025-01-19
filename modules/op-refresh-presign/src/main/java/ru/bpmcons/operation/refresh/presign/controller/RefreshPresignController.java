package ru.bpmcons.operation.refresh.presign.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bpmcons.operation.refresh.presign.RefreshPresignService;
import ru.bpmcons.operation.refresh.presign.dto.RefreshPresignRequest;
import ru.bpmcons.operation.refresh.presign.dto.RefreshPresignResponse;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshPresignController {
    private final RefreshPresignService presignService;

    @PostMapping("/refresh-presign")
    public RefreshPresignResponse presign(@Valid @RequestBody RefreshPresignRequest dto) {
        return presignService.refreshPresign(dto);
    }
}
