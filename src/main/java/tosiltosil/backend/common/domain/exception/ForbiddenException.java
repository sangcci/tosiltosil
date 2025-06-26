package tosiltosil.backend.common.domain.exception;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}
