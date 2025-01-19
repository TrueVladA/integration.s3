package ru.bpmcons.client.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.bpmcons.client.s3.domain.S3File;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(S3File file) {
        super("Файл " + file + " не найден");
    }
}
