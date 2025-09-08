package tosiltosil.backend.module.auth.domain.response.email;

public record EmailSendResponse(
        String email
) {
    public static EmailSendResponse of(
            final String email
    ) {
        return new EmailSendResponse(email);
    }
}
