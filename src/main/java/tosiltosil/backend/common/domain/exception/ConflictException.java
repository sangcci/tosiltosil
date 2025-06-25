package tosiltosil.backend.common.domain.exception;

public class ConflictException extends CustomException {

    private final ErrorCode errorCode;

    public ConflictException(String message) {
        super(new ErrorCode() {
            @Override
            public int status() {
                return 409;
            }
            @Override
            public String message() {
                return message;
            }
        });

        this.errorCode = new ErrorCode() {
            @Override
            public int status() {
                return 409;
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
