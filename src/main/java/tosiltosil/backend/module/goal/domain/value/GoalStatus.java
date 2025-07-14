package tosiltosil.backend.module.goal.domain.value;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GoalStatus {
    BEFORE_STARTING("시작 전"),
    RUNNING("진행 중"),
    PAUSED("일시 정지"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String koreanName;

    GoalStatus(final String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }
}
