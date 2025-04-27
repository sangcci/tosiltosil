package tosiltosil.backend.common.exception;

public interface ErrorCode {

    int httpStatus();

    String customCode();

    String title();

    String message();
}
