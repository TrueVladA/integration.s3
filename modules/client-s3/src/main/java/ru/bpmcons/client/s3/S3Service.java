package ru.bpmcons.client.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.s3.domain.Bucket;
import ru.bpmcons.client.s3.domain.Method;
import ru.bpmcons.client.s3.domain.OriginalPresignParams;
import ru.bpmcons.client.s3.domain.S3File;
import ru.bpmcons.client.s3.exception.FileNotFoundException;
import ru.bpmcons.client.s3.utils.MD5Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class S3Service {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssVV");

    private final S3Properties s3Properties;
    private final AmazonS3 client;

    private String resolveBucket(Bucket bucket) {
        return switch (bucket) {
            case ARCHIVE -> s3Properties.getArchiveBucket();
            case OPERATIVE -> s3Properties.getOperativeBucket();
        };
    }

    @NonNull
    public S3File findBucket(@NonNull S3File file) {
        if (client.doesObjectExist(resolveBucket(file.bucket()), file.id())) {
            return file;
        }
        if (client.doesObjectExist(s3Properties.getOperativeBucket(), file.id())) {
            return file.withBucket(Bucket.OPERATIVE);
        } else if (client.doesObjectExist(s3Properties.getArchiveBucket(), file.id())) {
            return file.withBucket(Bucket.ARCHIVE);
        } else {
            throw new FileNotFoundException(file);
        }
    }

    @NonNull
    public S3Object getObject(@NonNull S3File fileUrl) {
        S3Object object = client.getObject(resolveBucket(fileUrl.bucket()), fileUrl.id());
        if (object == null) {
            throw new FileNotFoundException(fileUrl);
        }
        return object;
    }

    public URL generatePresignedUrl(@NonNull S3File file, @NonNull Method method, @NonNull Map<String, String> attributes, @Nullable String contentMd5, @Nullable OriginalPresignParams params) {
        return execute(() -> {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(resolveBucket(file.bucket()), file.id());
            Date expiration = new Date();
            expiration.setTime(System.currentTimeMillis() + Integer.parseInt(s3Properties.getExpiredTime()));
            request.setExpiration(expiration);
            request.setMethod(method.toHttpMethod());
            attributes.forEach((k, v) -> request.addRequestParameter("X-ECM-Attr-" + k, v));
            request.addRequestParameter("X-ECM-Try", params == null ? "1" : String.valueOf(params.tries() + 1));
            if (params != null) {
                request.addRequestParameter("X-ECM-OrigDate", FORMAT.format(params.date().atZone(ZoneId.of("UTC"))));
            }
            if (contentMd5 != null && !contentMd5.isBlank()) {
                if (!MD5Utils.isValidHash(contentMd5)) {
                    throw new IllegalArgumentException("MD5 hash invalid");
                }
                request.setContentMd5(contentMd5);
                request.addRequestParameter("X-ECM-OrigContentMD5", contentMd5);
            }
            URL url = client.generatePresignedUrl(request);
            try {
                String fileUrl = url.getFile();
                if (s3Properties.isBreakPresign()) {
                    fileUrl += "&X-ECM-Broken=1";
                }
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), fileUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void moveFile(@NonNull String id, Bucket from, Bucket to) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest();
        copyObjectRequest.setSourceBucketName(resolveBucket(from));
        copyObjectRequest.setSourceKey(id);
        copyObjectRequest.setDestinationBucketName(resolveBucket(to));
        copyObjectRequest.setDestinationKey(id);
        try {
            execute(() -> client.copyObject(copyObjectRequest));
        } catch (AmazonS3Exception e) {
            S3Object deletedBeforeFile = client.getObject(resolveBucket(to), id);
            if (deletedBeforeFile != null) {
                return;
            } else {
                throw e;
            }
        }
        client.deleteObject(resolveBucket(from), id);
    }

    @SneakyThrows
    private <T> T execute(Supplier<T> supplier) {
        int tries = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (SdkClientException e) {
                tries += 1;
                if (s3Properties.getRequestRetries() > tries) {
                    throw e;
                }
                Thread.sleep(s3Properties.getRequestDelay().toMillis());
            }
        }
    }
}
