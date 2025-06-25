package tosiltosil.backend.common.web.response;

import java.util.Map;

public record Response<T>(
        int status,
        String message,
        T data
) {

    public static <T> Response<T> ok(final String message, final T data) {
        return new Response<>(200, message, data);
    }

    public static Response<Map<String, Object>> ok(final String message) {
        return new Response<>(200, message, Map.of());
    }

    public static <T> Response<T> create(final String message, final T data) {
        return new Response<>(201, message, data);
    }

    public static Response<Map<String, Object>> create(final String message) {
        return new Response<>(201, message, Map.of());
    }
}
