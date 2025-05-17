package tosiltosil.backend.common.web.response;

public record Response<T>(
        String message,
        T data
) {

    public static <T> Response<T> ok(T data) {
        return new Response<>("OK", data);
    }
}
