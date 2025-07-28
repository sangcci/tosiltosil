package tosiltosil.backend.module.email.domain.response;

public record EmailSendResponse(
        String email
) {
    public static EmailSendResponse of(
            final String email
    ) {
        return new EmailSendResponse(email);
    }
}
