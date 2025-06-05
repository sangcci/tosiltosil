package tosiltosil.backend.common.domain.exception;

public abstract class CustomException extends RuntimeException {

    protected CustomException(final ErrorCode errorCode) {
        super("%s".formatted(errorCode.message()));
    }

    public abstract ErrorCode getErrorCode();
}
