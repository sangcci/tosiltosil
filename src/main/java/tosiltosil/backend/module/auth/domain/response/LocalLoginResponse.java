package tosiltosil.backend.module.auth.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public record LocalLoginResponse(
        UUID memberId,

        @JsonIgnore
        String accessToken,

        @JsonIgnore
        String refreshToken

        ) {
    public static LocalLoginResponse of(
            final UUID memberId,
            final String accessToken,
            final String refreshToken
    ) {
        return new LocalLoginResponse(memberId, accessToken, refreshToken);
    }
}
