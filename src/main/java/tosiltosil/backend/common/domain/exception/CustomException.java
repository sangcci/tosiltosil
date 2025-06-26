package tosiltosil.backend.common.domain.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {

    private final int status;

    protected CustomException(final int status, final String message) {
        super(message);
        this.status = status;
    }
}