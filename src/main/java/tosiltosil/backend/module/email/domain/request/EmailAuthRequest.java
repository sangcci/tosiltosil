package tosiltosil.backend.module.email.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailAuthRequest(
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "인증번호 (6자리 숫자)", example = "123456", minLength = 6, maxLength = 6)
        @NotBlank(message = "인증번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "인증번호는 6자리 숫자로 입력해주세요."
        )
        String authNumber
) {
}
