package tosiltosil.backend.module.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Duration totalTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private Long iconId;

    @Column(nullable = false)
    private LocalDate date;

    @Builder
    private Goal(
            final UUID memberId,
            final Long categoryId,
            final String title,
            final Duration totalTime,
            final GoalStatus status,
            final Duration duration,
            final LocalDate date,
            final Long iconId,
            final int sequence
    ) {
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.title = title;
        this.totalTime = totalTime;
        this.status = status;
        this.duration = duration;
        this.date = date;
        this.iconId = iconId;
        this.sequence = sequence;
    }

    public static Goal of(
            final UUID memberId,
            final Long categoryId,
            final String title,
            final Duration totalTime,
            final int sequence,
            final Long iconId,
            final LocalDate date
    ) {
        return Goal.builder()
                .memberId(memberId)
                .categoryId(categoryId)
                .title(title)
                .totalTime(totalTime)
                .status(GoalStatus.BEFORE_STARTING)
                .duration(Duration.ZERO)
                .sequence(sequence)
                .iconId(iconId)
                .date(date)
                .build();
    }

    public void updateBasicInfo(
            final String title,
            final Long categoryId,
            final Long iconId
    ) {
        this.title = title;
        this.iconId = iconId;
        this.categoryId = categoryId;
    }

    public void changeDate(final LocalDate date) {
        this.date = date;
    }

    public void changeStatusToStarted() {
        if (this.status == GoalStatus.BEFORE_STARTING || this.status == GoalStatus.PAUSED) {
            this.status = GoalStatus.RUNNING;
        } else {
            throw new IllegalStateException("스톱워치가 이미 실행되거나 기간이 지난 상태입니다.");
        }
    }

    public void changeStatusToPaused() {
        if (this.status == GoalStatus.RUNNING) {
            this.status = GoalStatus.PAUSED;
        } else {
            throw new IllegalStateException("스톱워치가 이미 정지되었습니다.");
        }
    }

    public void changeStatusToCompleted() {
        if (this.status == GoalStatus.RUNNING) {
            this.status = GoalStatus.COMPLETED;
        } else {
            throw new IllegalStateException("실행 중이 아닌 목표는 완료할 수 없습니다.");
        }
    }

    public void addDuration(final Duration addedDuration) {
        this.duration = this.duration.plus(addedDuration);
    }
}