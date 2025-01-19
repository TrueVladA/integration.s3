package ru.bpmcons.operation.archive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bpmcons.client.s3.S3Service;
import ru.bpmcons.client.s3.domain.Bucket;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {
    private final S3Service s3;

    public void archive(String file) {
        s3.moveFile(file, Bucket.OPERATIVE, Bucket.ARCHIVE);
    }

    public void unarchive(String file) {
        s3.moveFile(file, Bucket.ARCHIVE, Bucket.OPERATIVE);
    }
}
