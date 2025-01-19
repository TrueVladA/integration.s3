package ru.bpmcons.preview.base.dto;

import org.springframework.lang.Nullable;
import ru.bpmcons.client.s3.domain.Bucket;

public record GeneratePreviewRequest(
        @Nullable
        Bucket bucket,
        String fileExt
) {
}
