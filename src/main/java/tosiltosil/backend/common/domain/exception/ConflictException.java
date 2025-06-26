package tosiltosil.backend.common.domain.exception;

public class ConflictException extends CustomException {

    public ConflictException(String message) {
        super(409, message);
    }
}
