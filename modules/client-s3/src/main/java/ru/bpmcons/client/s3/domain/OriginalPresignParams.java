package ru.bpmcons.client.s3.domain;

import java.time.Instant;

public record OriginalPresignParams(
        Instant date,
        int tries
) {
}
