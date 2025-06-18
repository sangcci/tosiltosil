package tosiltosil.backend.module.terms.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TermsDetail(
        @NotBlank(message = "약관 제목을 입력해주세요.")
        String title,

        @NotBlank(message = "버전을 입력해주세요.")
        String version,

        @NotNull(message = "필수 여부를 입력해주세요.")
        boolean required,

        @NotNull(message = "동의 여부를 입력해주세요.")
        boolean agreed
) {

}
