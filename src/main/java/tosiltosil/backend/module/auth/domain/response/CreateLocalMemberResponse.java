package tosiltosil.backend.module.auth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateLocalMemberResponse(
        @Schema(description = "사용자 닉네임", example = "유저1")
        String nickname
) {
    public static CreateLocalMemberResponse of(final String nickname) {
        return new CreateLocalMemberResponse(nickname);
    }
}
