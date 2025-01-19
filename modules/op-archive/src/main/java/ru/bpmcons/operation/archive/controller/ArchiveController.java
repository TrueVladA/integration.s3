package ru.bpmcons.operation.archive.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bpmcons.operation.archive.ArchiveService;

@Slf4j
@RestController
@RequestMapping("/api/files/{id}")
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("/archive")
    public void archive(@PathVariable("id") String id) {
        archiveService.archive(id);
    }

    @PostMapping("/unarchive")
    public void unarchive(@PathVariable("id") String id) {
        archiveService.unarchive(id);
    }
}
