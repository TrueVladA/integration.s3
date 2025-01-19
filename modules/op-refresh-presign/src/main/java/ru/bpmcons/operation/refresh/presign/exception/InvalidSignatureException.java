package ru.bpmcons.operation.refresh.presign.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException() {
        super("Неверная подпись у presign-ссылки");
    }
}
