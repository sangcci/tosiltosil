package tosiltosil.backend.common.domain;

public interface ErrorCode {

    int httpStatus();

    String customCode();

    String title();

    String message();
}
