package tosiltosil.backend.module.email.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import tosiltosil.backend.common.domain.validator.IsEnum;
import tosiltosil.backend.module.email.domain.value.EmailAuthPurpose;

public record EmailSendRequest(
        @NotBlank(message="이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @IsEnum(enumClass = EmailAuthPurpose.class)
        String purpose
) {
}
