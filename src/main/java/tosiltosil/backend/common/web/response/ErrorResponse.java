package tosiltosil.backend.common.web.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        @JsonInclude(NON_NULL) List<ErrorDetailResponse> errors
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
        return new ErrorResponse(status, message, null);
    }

    public static ErrorResponse of(final int status, final String message, final List<ErrorDetailResponse> errorDetailResponses) {
        return new ErrorResponse(status, message, errorDetailResponses);
    }
}
