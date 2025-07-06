package tosiltosil.backend.module.auth.domain.response;

public record CreateLocalMemberResponse(
        String nickname
) {
    public static CreateLocalMemberResponse of(final String nickname) {
        return new CreateLocalMemberResponse(nickname);
    }
}
