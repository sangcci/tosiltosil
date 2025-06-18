package tosiltosil.backend.module.auth.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tosiltosil.backend.common.auth.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;
import java.util.UUID;

public record CreateLocalMemberRequest(
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증번호를 입력해주세요.")
        String authNumber,

        @NotBlank(message="비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message="비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요."
        )
        @Size(min=8, message = "비밀번호는 8글자 이상으로 입력해주세요.")
        String password,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{2,8}$",
                message="닉네임은 한글, 영문, 숫자를 포함하여 2글자 이상 8글자 이하로 입력해주세요."
        )
        @Size(min=2, max=8, message = "닉네임은 2글자 이상, 8글자 이하로 입력해주세요")
        String nickname,

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
