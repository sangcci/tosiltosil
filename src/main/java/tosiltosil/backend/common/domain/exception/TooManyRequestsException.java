package tosiltosil.backend.common.domain.exception;

public class TooManyRequestsException extends CustomException{

    public TooManyRequestsException(String message) {
        super(429, message);
    }
}
