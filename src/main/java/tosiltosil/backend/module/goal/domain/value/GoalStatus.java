package tosiltosil.backend.module.goal.domain.value;

import com.fasterxml.jackson.annotation.JsonValue;
import tosiltosil.backend.common.domain.exception.ConflictException;

public enum GoalStatus {
    BEFORE_STARTING("시작 전"),
    RUNNING("진행 중"),
    PAUSED("진행 중"),
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

    public void validateRunning() {
        validateNotCompleted();
        validateNotFailed();
        validateNotRunning();
    }

    public void validatePaused() {
        validateNotCompleted();
        validateNotFailed();
        validateNotPaused();
    }

    private void validateNotCompleted() {
        if (this.equals(GoalStatus.COMPLETED)) {
            throw new ConflictException("이미 완료된 목표입니다.");
        }
    }

    private void validateNotFailed() {
        if (this.equals(GoalStatus.FAILED)) {
            throw new ConflictException("기간이 지나 실패한 목표입니다.");
        }
    }

    private void validateNotRunning() {
        if (this.equals(GoalStatus.RUNNING)) {
            throw new ConflictException("스톱워치가 이미 실행중입니다.");
        }
    }

    private void validateNotPaused() {
        if (this.equals(GoalStatus.BEFORE_STARTING) || this.equals(GoalStatus.PAUSED)) {
            throw new ConflictException("스톱워치가 이미 정지되었습니다.");
        }
    }
}
