package tosiltosil.backend.common.auth.domain.response;

public record TokenPair(
        String accessTokenInfo,
        String refreshTokenInfo
) {
    public static TokenPair of(String accessTokenInfo, String refreshTokenInfo) {
        return new TokenPair(accessTokenInfo, refreshTokenInfo);
    }
}
