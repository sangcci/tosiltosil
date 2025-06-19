package tosiltosil.backend.common.domain.exception;

public class NotFoundException extends CustomException {

    private final ErrorCode errorCode;

    public NotFoundException(String message) {
        super(new ErrorCode() {
            @Override
            public int status() {
                return 404;
            }
            @Override
            public String message() {
                return message;
            }
        });

        this.errorCode = new ErrorCode() {
            @Override
            public int status() {
                return 404;
            }

            @Override
            public String message() {
                return message;
            }
        };
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
