package tosiltosil.backend.module.auth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;
import java.util.UUID;

public record CreateLocalMemberRequest(
        @Schema(description = "이메일 주소", example = "user@example.com", format = "email")
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "인증번호 (6자리 숫자)", example = "123456", minLength = 6, maxLength = 6)
        @NotBlank(message = "인증번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "인증번호는 6자리 숫자로 입력해주세요."
        )
        String authNumber,

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자를 포함해 8글자 이상)", example = "qwer1234!", minLength = 8)
        @NotBlank(message="비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message="비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요."
        )
        String password,

        @Schema(description = "닉네임 (한글, 영문, 숫자 포함해 2글자 이상, 8글자 이하)", example = "유저1", minLength = 2, maxLength = 8)
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{2,8}$",
                message="닉네임은 한글, 영문, 숫자를 포함하여 2글자 이상 8글자 이하로 입력해주세요."
        )
        String nickname,

        @Schema(description = "약관 목록 (termsOfService, privacyPolicy, ageConfirmation)", minLength = 3, maxLength = 3)
        @NotNull(message = "약관 동의 여부를 입력해주세요.")
        List<TermsDetail> terms
) {
        public Member toMemberEntities(
                final String code,
                final String profileImageUrl
        ) {
                return Member.of(
                        nickname,
                        code,
                        profileImageUrl,
                        LoginType.LOCAL,
                        email
                );
        }

        public LocalAccount toLocalAccountEntities(
                final UUID memberId,
                final String encryptedPassword
        ) {
                return LocalAccount.of(
                        memberId,
                        encryptedPassword
                );
        }
}
