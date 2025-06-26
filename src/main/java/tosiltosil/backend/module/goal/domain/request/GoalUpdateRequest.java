package tosiltosil.backend.module.goal.domain.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;

public record GoalUpdateRequest(
        @NotBlank(message = "제목은 1글자 이상 20글자 이하여야 합니다.")
        @Size(min = 1, max = 20, message = "제목은 1글자 이상 20글자 이하여야 합니다.")
        String title,

        @NotNull(message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        @Min(value = 1, message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        @Max(value = 12, message = "아이콘은 지정된 숫자 1~12 이내여야 합니다.")
        Long iconId,
        
        Long categoryId,
        
        @NotNull(message = "목표 날짜는 필수입니다.")
        @FutureOrPresent(message = "목표 적용 기간은 무조건 오늘 이후여야 합니다.")
        LocalDate date,
        
        @NotNull(message = "목표 타이머는 필수입니다.")
        @DurationMin(minutes = 1, message = "시간은 0시 1분 이상 23시 59분 이하여아 합니다.")
        @DurationMax(hours = 23, minutes = 59, seconds = 59, message = "시간은 0시 1분 이상 23시 59분 이하여아 합니다.")
        Duration time
) {

}
