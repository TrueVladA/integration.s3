package ru.bpmcons.operation.refresh.presign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.s3.S3Properties;
import ru.bpmcons.client.s3.S3Service;
import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.OriginalPresignParams;
import ru.bpmcons.client.s3.domain.S3File;
import ru.bpmcons.client.s3.service.SignatureVerifyService;
import ru.bpmcons.operation.refresh.presign.dto.RefreshPresignRequest;
import ru.bpmcons.operation.refresh.presign.dto.RefreshPresignResponse;
import ru.bpmcons.operation.refresh.presign.exception.InvalidSignatureException;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class RefreshPresignService {
    private final SignatureVerifyService signatureVerifyService;
    private final S3Service s3;
    private final S3Properties properties;

    public RefreshPresignResponse refreshPresign(RefreshPresignRequest dto) {
        if (!signatureVerifyService.verifySignature(dto.url(), dto.method())) {
            throw new InvalidSignatureException();
        }
        SignatureVerifyService.ParsedUrl url = signatureVerifyService.parseUrl(dto.url());
        Bucket bucket = url.bucket().equals(properties.getArchiveBucket()) ? Bucket.ARCHIVE : Bucket.OPERATIVE;
        URL response = s3.generatePresignedUrl(new S3File(bucket, url.fileId()), dto.method(), url.attributes(), url.contentMd5(), new OriginalPresignParams(
                url.originalDate(),
                url.tries()
        ));
        return new RefreshPresignResponse(response.toString());
    }
}
