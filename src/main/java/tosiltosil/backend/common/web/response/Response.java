package tosiltosil.backend.common.web.response;

public record Response<T>(
        int status,
        String message,
        T data
) {

    public static <T> Response<T> ok(final String message, final T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> create(final String message, final T data) {
        return new Response<>(201, message, data);
    }
}
