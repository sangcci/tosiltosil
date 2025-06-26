package tosiltosil.backend.common.domain.exception;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(404, message);
    }
}
