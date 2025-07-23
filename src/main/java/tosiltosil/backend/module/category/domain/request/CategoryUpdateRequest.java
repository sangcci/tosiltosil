package tosiltosil.backend.module.category.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tosiltosil.backend.common.domain.validator.IsEnum;
import tosiltosil.backend.module.category.domain.value.CategoryColor;

public record CategoryUpdateRequest(
        @NotBlank(message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        @Size(min = 1, max = 10, message = "제목은 1글자 이상 10글자 이하여야 합니다.")
        String title,

        @IsEnum(enumClass = CategoryColor.class)
        String color
) {

}
