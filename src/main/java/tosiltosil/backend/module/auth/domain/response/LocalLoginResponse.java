package tosiltosil.backend.module.auth.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

public record LocalLoginResponse(
        @Schema(description = "사용자 ID", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        UUID memberId,

        @JsonIgnore
        HttpHeaders headers

        ) {
    public static LocalLoginResponse of(
            final UUID memberId,
            final HttpHeaders headers
    ) {
        return new LocalLoginResponse(memberId, headers);
    }
}
