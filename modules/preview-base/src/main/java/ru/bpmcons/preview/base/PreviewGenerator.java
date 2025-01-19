package ru.bpmcons.preview.base;

import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface PreviewGenerator {
    boolean isApplicableTo(@NonNull String fileExt);

    @NonNull
    ByteArrayOutputStream generate(@NonNull InputStream inputStream);
}
