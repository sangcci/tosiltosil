package tosiltosil.backend.module.email.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record EmailAuthResponse(
        @JsonIgnore
        String temporaryToken
) {
    public static EmailAuthResponse of(final String temporaryToken) {
        return new EmailAuthResponse(temporaryToken);
    }
}
