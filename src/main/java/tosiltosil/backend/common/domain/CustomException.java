package tosiltosil.backend.common.domain;

public abstract class CustomException extends RuntimeException {

    protected CustomException(final ErrorCode errorCode) {
        super("%s".formatted(errorCode.message()));
    }

    public abstract ErrorCode getErrorCode();
}
