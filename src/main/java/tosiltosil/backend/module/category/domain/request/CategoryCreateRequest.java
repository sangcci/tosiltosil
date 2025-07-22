package tosiltosil.backend.module.category.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

import tosiltosil.backend.common.domain.validator.IsEnum;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.value.CategoryColor;

public record CategoryCreateRequest(
        @NotBlank(message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        @Size(min = 1, max = 10, message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        String title,

        @IsEnum(enumClass = CategoryColor.class)
        String color
) {
    
    public Category toEntity(
            final UUID memberId,
            final BigDecimal orderIndex
    ) {
        return Category.of(memberId, title, CategoryColor.valueOf(color), orderIndex);
    }
}