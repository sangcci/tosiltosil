package tosiltosil.backend.module.auth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LocalLoginRequest (
        @Schema(description = "이메일 주소", example = "user@example.com", format = "email")
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자를 포함해 8글자 이상)", example = "qwer1234!", minLength = 8)
        @NotBlank(message="비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message="비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요."
        )
        String password
){
}
