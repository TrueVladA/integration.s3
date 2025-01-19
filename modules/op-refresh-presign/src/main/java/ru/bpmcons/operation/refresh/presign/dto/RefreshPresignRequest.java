package ru.bpmcons.operation.refresh.presign.dto;

import ru.bpmcons.client.s3.domain.Method;

public record RefreshPresignRequest(
        String url,
        Method method
) {
}
