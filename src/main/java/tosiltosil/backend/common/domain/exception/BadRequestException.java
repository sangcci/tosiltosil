package tosiltosil.backend.common.domain.exception;

public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super(400, message);
    }
}