package tosiltosil.backend.common.web.response;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<ErrorDetailResponse> errors
) {

    public record ErrorDetailResponse(
            String field,
            String value,
            String reason
    ) {

        public static ErrorDetailResponse of(final String field, final String value, final String reason) {
            return new ErrorDetailResponse(field, value, reason);
        }
    }

    public static ErrorResponse of(final int status, final String message) {
        return new ErrorResponse(status, message, List.of());
    }

    public static ErrorResponse of(final int status, final String message, final List<ErrorDetailResponse> errorDetailResponses) {
        return new ErrorResponse(status, message, errorDetailResponses);
    }
}
