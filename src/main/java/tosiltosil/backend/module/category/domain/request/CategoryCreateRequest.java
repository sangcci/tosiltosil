package tosiltosil.backend.module.category.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import tosiltosil.backend.module.category.domain.Category;

public record CategoryCreateRequest(
        @NotBlank(message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        @Size(min = 1, max = 10, message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        String title,
        
        @NotBlank(message = "색깔은 필수입니다.")
        String color
) {
    
    public Category toEntity(
            final UUID memberId,
            final String orderKey
    ) {
        return Category.of(memberId, title, color, orderKey);
    }
}