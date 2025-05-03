package tosiltosil.backend.common.web.handler;

public record ErrorResponse(
        String customCode,
        String title,
        String message
) {

}
