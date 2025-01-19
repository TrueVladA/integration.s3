package ru.bpmcons.operation.presign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.s3.S3Service;
import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.S3File;
import ru.bpmcons.operation.presign.dto.PresignRequest;
import ru.bpmcons.operation.presign.dto.PresignResponse;

import java.net.URL;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresignService {
    private final S3Service s3;

    public PresignResponse createPresign(String id, PresignRequest request) {
        S3File file;
        if (request.bucket() == null) {
            file = s3.findBucket(new S3File(Bucket.OPERATIVE, id));
        } else {
            file = new S3File(request.bucket(), id);
        }

        log.debug("Запрос preSign url файла " + file);
        URL presignedUrl = s3.generatePresignedUrl(file, request.method(), request.attributes() == null ? Map.of() : request.attributes(), request.contentMd5(), null);
        log.debug("Ответ от s3 хранилища на " + request.method() + " запрос: " + presignedUrl.toString());

        return new PresignResponse(file.bucket(), file.id(), presignedUrl.toString(), request.method());
    }

}
