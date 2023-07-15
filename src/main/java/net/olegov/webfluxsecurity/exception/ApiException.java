package net.olegov.webfluxsecurity.exception;

import lombok.Builder;
public class ApiException extends RuntimeException {

    protected String errorCode;
    protected String message;

    @Builder
    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
