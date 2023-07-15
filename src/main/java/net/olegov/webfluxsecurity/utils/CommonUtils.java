package net.olegov.webfluxsecurity.utils;

import java.util.Base64;
import net.olegov.webfluxsecurity.constants.ErrorCode;
import net.olegov.webfluxsecurity.exception.AuthException;
import reactor.core.publisher.Mono;

public class CommonUtils {

    public static String toBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static AuthException generateAuthException(ErrorCode errorCode) {
        return AuthException.builder()
                         .errorCode(errorCode.getCode())
                         .message(errorCode.getMessage())
                         .build();
    }

}
