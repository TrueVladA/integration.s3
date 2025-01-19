package ru.bpmcons.operation.presign.dto;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.Method;
import ru.bpmcons.client.s3.utils.ValidMD5Hash;

import java.util.Map;

public record PresignRequest(
        @Nullable Bucket bucket,
        @NonNull Method method,
        @Nullable @ValidMD5Hash String contentMd5,
        @Nullable Map<String, String> attributes
) {
}
