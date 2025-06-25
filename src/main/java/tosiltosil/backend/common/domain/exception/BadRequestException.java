package tosiltosil.backend.common.domain.exception;

public class BadRequestException extends CustomException {

    private final ErrorCode errorCode;

    public BadRequestException(String message) {
        super(new ErrorCode() {
            @Override
            public int status() {
                return 400;
            }
            @Override
            public String message() {
                return message;
            }
        });

        this.errorCode = new ErrorCode() {
            @Override
            public int status() {
                return 400;
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
