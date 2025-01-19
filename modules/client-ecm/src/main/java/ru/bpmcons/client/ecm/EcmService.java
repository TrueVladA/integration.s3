package ru.bpmcons.client.ecm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.bpmcons.client.ecm.dto.EcmFileResponse;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EcmService {
    private final EcmProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        restTemplate.getMessageConverters().removeIf(httpMessageConverter -> httpMessageConverter instanceof MappingJackson2HttpMessageConverter);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.parseMediaType("text/plain;charset=utf-8"),
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.APPLICATION_JSON
        ));
        restTemplate.getMessageConverters().add(converter);
    }

    public String uploadFile(String uuid, byte[] bytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(properties.getBearerAuth());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Content-Range", "bytes 0-" + bytes.length + "/" + bytes.length);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(uuid + ".jpeg")
                .build().toString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new HttpEntity<>(bytes, fileMap));


        ResponseEntity<EcmFileResponse> response = restTemplate.postForEntity(
                properties.getPathToDisk() + "/upload?hash=" + uuid,
                new HttpEntity<>(body, headers),
                EcmFileResponse.class
        );
        if (response.getBody() == null) {
            throw new IllegalStateException("ECM error response: " + response);
        }
        return response.getBody().getFile().getId();
    }
}
