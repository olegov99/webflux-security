package net.olegov.webfluxsecurity.constants;

public enum ErrorCode {

    ACCOUNT_IS_DISABLED("WFS-0001", "Account is disabled"),
    INVALID_PASSWORD("WFS-0002", "Invalid password"),
    INVALID_USERNAME("WFS-0003", "Invalid username"),
    EXPIRED_TOKEN("WFS-0004", "Token has been expired");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return String.format("[error_code=%s] [%s]", getCode(), getMessage());
    }

}
