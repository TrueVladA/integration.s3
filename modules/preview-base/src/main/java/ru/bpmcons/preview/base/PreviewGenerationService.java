package ru.bpmcons.preview.base;

import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.ecm.EcmService;
import ru.bpmcons.client.s3.S3Service;
import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.S3File;
import ru.bpmcons.preview.base.domain.PreviewFile;
import ru.bpmcons.preview.base.dto.GeneratePreviewRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewGenerationService {
    private final List<PreviewGenerator> generators;
    private final S3Service s3Service;
    private final EcmService ecmService;

    @NonNull
    @SneakyThrows
    public ByteArrayOutputStream generatePreview(@NonNull String fileExt, @NonNull InputStream file) {
        PreviewGenerator generator = generators.stream()
                .filter(previewGenerator -> previewGenerator.isApplicableTo(fileExt))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("File extension " + fileExt + " not supported"));
        try (file) {
            return generator.generate(file);
        }
    }

    @Nullable
    @SneakyThrows
    public PreviewFile uploadPreview(String fileId, GeneratePreviewRequest request) {
        S3File newFileUrl = s3Service.findBucket(request.bucket() == null ? new S3File(Bucket.OPERATIVE, fileId) : new S3File(request.bucket(), fileId));
        S3Object object = s3Service.getObject(newFileUrl);
        try (InputStream inputStream = object.getObjectContent().getDelegateStream()) {
            UUID previewId = UUID.randomUUID();
            ByteArrayOutputStream preview = generatePreview(request.fileExt(), inputStream);
            String previewEcmId = ecmService.uploadFile(previewId.toString(), preview.toByteArray());

            return new PreviewFile(previewEcmId, previewId.toString());
        }
    }
}
