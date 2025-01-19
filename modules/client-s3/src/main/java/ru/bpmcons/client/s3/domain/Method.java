package ru.bpmcons.client.s3.domain;

import com.amazonaws.HttpMethod;
import com.amazonaws.http.HttpMethodName;

public enum Method {
    GET,
    PUT;

    public HttpMethod toHttpMethod() {
        return switch (this) {
            case GET -> HttpMethod.GET;
            case PUT -> HttpMethod.PUT;
        };
    }

    public HttpMethodName toHttpMethodName() {
        return switch (this) {
            case GET -> HttpMethodName.GET;
            case PUT -> HttpMethodName.PUT;
        };
    }
}
