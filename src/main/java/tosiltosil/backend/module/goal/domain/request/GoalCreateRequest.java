package tosiltosil.backend.module.goal.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import tosiltosil.backend.common.domain.validator.IsDate;
import tosiltosil.backend.common.domain.validator.IsDuration;

public record GoalCreateRequest(
        @NotBlank(message = "제목은 1글자 이상 20글자 이하여야 합니다.")
        @Size(min = 1, max = 20, message = "제목은 1글자 이상 20글자 이하여야 합니다.")
        String title,
        
        @NotNull(message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        @Min(value = 1, message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        @Max(value = 12, message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        Long iconId,
        
        Long categoryId,
        
        @NotEmpty(message = "목표 적용 기간은 무조건 1개 이상이어야 합니다.")
        List<@IsDate String> dates,
        
        @NotNull(message = "목표 타이머는 필수입니다.")
        @IsDuration
        String time
) {

}
