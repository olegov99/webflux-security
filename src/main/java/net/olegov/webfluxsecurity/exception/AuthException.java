package net.olegov.webfluxsecurity.exception;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

public class AuthException extends ApiException {

    @Builder
    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
