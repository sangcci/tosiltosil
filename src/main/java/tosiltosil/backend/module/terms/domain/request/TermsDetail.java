package tosiltosil.backend.module.terms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tosiltosil.backend.module.terms.domain.MemberTerms;

import java.util.UUID;

public record TermsDetail(
        @Schema(description = "약관 제목", example = "termsOfService")
        @NotBlank(message = "약관 제목을 입력해주세요.")
        String title,

        @Schema(description = "버전", example = "0.1.0")
        @NotBlank(message = "버전을 입력해주세요.")
        String version,

        @Schema(description = "사용자 동의 여부", example = "true(동의), false(미동의)")
        @NotNull(message = "동의 여부를 입력해주세요.")
        boolean agreed
) {
        public MemberTerms toEntities(
                final UUID memberId,
                final Long termsVersionId
        ) {
                return MemberTerms.of(
                        memberId,
                        termsVersionId,
                        agreed
                );
        }
}
