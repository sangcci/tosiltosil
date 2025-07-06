package tosiltosil.backend.module.email.domain.response;

import java.util.UUID;

public record EmailSendResponse(
        String email,
        UUID clientId
) {
    public static EmailSendResponse of(
            final String email,
            final UUID clientId
    ) {
        return new EmailSendResponse(email, clientId);
    }
}
