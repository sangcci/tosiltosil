package tosiltosil.backend.module.auth.domain.request.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailAuthRequest(
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "인증번호는 6자리 숫자로 입력해주세요."
        )
        String authNumber
) {
}
