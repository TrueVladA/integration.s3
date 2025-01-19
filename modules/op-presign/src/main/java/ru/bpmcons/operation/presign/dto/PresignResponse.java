package ru.bpmcons.operation.presign.dto;

import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.Method;

public record PresignResponse(
        Bucket bucket,
        String file,
        String presignUrl,
        Method method
) {
}
