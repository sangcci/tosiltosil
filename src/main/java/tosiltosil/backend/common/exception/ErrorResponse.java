package tosiltosil.backend.common.exception;

public record ErrorResponse(
        String customCode,
        String title,
        String message
) {

}
