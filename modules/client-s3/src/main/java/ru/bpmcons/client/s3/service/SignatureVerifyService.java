package ru.bpmcons.client.s3.service;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.SdkClock;
import com.amazonaws.auth.internal.SignerConstants;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.s3.S3Properties;
import ru.bpmcons.client.s3.S3Service;
import ru.bpmcons.client.s3.domain.Method;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SignatureVerifyService {
    private final AWSCredentials credentials;
    private final S3Properties properties;
    private final MockClock clock = new MockClock();

    private final ThreadLocal<AWSS3V4Signer> signer;

    @SneakyThrows
    public SignatureVerifyService(S3Properties properties) {
        this.credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        this.properties = properties;

        signer = ThreadLocal.withInitial(() -> buildSigner(properties));
    }

    @SneakyThrows
    private AWSS3V4Signer buildSigner(S3Properties properties) {
        AWSS3V4Signer signer = new AWSS3V4Signer();

        signer.setServiceName(AmazonS3Client.S3_SERVICE_NAME);
        signer.setRegionName(properties.getRegion());
        Field clkField = AWS4Signer.class.getDeclaredField("clock");
        clkField.setAccessible(true);
        clkField.set(signer, clock);
        return signer;
    }

    public ParsedUrl parseUrl(String url) {
        return ParsedUrl.parse(url, properties);
    }

    public boolean verifySignature(String url, Method method) {
        ParsedUrl parsed = ParsedUrl.parse(url, properties);

        if (properties.isBreakPresign() || properties.isVerifyBrokenUrls()) {
            parsed.params().remove("X-ECM-Broken");
        }

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(parsed.bucket(), parsed.fileId(), method.toHttpMethod());
        Date expiration = new Date();
        expiration.setTime(parsed.date.getTime() + parsed.expires * 1000);
        req.setExpiration(expiration);
        parsed.params().forEach(req::addRequestParameter);
        if (parsed.contentMd5() != null) {
            req.setContentMd5(parsed.contentMd5());
            req.addRequestParameter("X-ECM-OrigContentMD5", parsed.contentMd5());
        }

        DefaultRequest<GeneratePresignedUrlRequest> request = new DefaultRequest<>(req, Constants.S3_SERVICE_DISPLAY_NAME);
        request.setEndpoint(parsed.endpoint());
        request.setHttpMethod(method.toHttpMethodName());
        request.setResourcePath(parsed.path());
        if (parsed.contentMd5() != null) {
            request.addHeader("Content-MD5", parsed.contentMd5());
        }
        parsed.params().forEach(request::addParameter);
        AWSS3V4Signer signer = this.signer.get();
        signer.setOverrideDate(parsed.date());
        clock.setTime(parsed.date.toInstant());
        signer.presignRequest(request, credentials, expiration);

        String sign = request.getParameters().get(SignerConstants.X_AMZ_SIGNATURE).get(0);
        return sign.equals(parsed.signature());
    }

    public record ParsedUrl(
            String bucket,
            String fileId,
            Date date,
            long expires,
            String signature,
            URI endpoint,
            String path,
            Map<String, String> params,
            Map<String, String> attributes,
            int tries,
            String contentMd5,
            Instant originalDate
    ) {
        private static ParsedUrl parse(String url, S3Properties properties) {
            String bucket = url.contains(properties.getArchiveBucket()) ? properties.getArchiveBucket() : properties.getOperativeBucket();
            URI uri = URI.create(url);
            String[] path = uri.getPath().split("/");
            List<NameValuePair> query = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
            String dateStr = query.stream().filter(nameValuePair -> nameValuePair.getName().equals("X-Amz-Date")).findAny().orElseThrow().getValue();
            long expires = Long.parseLong(query.stream().filter(nameValuePair -> nameValuePair.getName().equals("X-Amz-Expires")).findAny().orElseThrow().getValue());
            String sign = query.stream().filter(nameValuePair -> nameValuePair.getName().equals("X-Amz-Signature")).findAny().orElseThrow().getValue();
            Instant date = S3Service.FORMAT.parse(dateStr, Instant::from);
            return new ParsedUrl(
                    bucket,
                    path[path.length - 1],
                    Date.from(date),
                    expires,
                    sign,
                    URI.create(uri.getScheme() + "://" + uri.getHost()),
                    uri.getPath(),
                    query.stream()
                            .filter(nameValuePair -> nameValuePair.getName().startsWith("X-ECM"))
                            .collect(Collectors.toMap(
                                    NameValuePair::getName,
                                    NameValuePair::getValue
                            )),
                    query.stream()
                            .filter(nameValuePair -> nameValuePair.getName().startsWith("X-ECM-Attr-"))
                            .collect(Collectors.toMap(
                                    p -> p.getName().replace("X-ECM-Attr-", ""),
                                    NameValuePair::getValue
                            )),
                    query.stream()
                            .filter(nameValuePair -> nameValuePair.getName().equals("X-ECM-Try"))
                            .findAny()
                            .map(s -> Integer.parseInt(s.getValue()))
                            .orElse(1),
                    query.stream()
                            .filter(nameValuePair -> nameValuePair.getName().equals("X-ECM-OrigContentMD5"))
                            .findAny()
                            .map(NameValuePair::getValue)
                            .orElse(null),
                    query.stream()
                            .filter(nameValuePair -> nameValuePair.getName().equals("X-ECM-OrigDate"))
                            .findAny()
                            .map(s -> S3Service.FORMAT.parse(s.getValue(), Instant::from))
                            .orElse(date)
            );
        }
    }

    private static final class MockClock implements SdkClock {
        private final ThreadLocal<Instant> time = ThreadLocal.withInitial(Instant::now);

        @Override
        public long currentTimeMillis() {
            return time.get().toEpochMilli();
        }

        public void setTime(Instant time) {
            this.time.set(time);
        }
    }
}
