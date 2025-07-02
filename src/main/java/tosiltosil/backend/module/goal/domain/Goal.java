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
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.ForbiddenException;
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
        validateTotalTime(totalTime);
        validateDate(date);
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

    private static void validateTotalTime(final Duration totalTime) {
        Duration oneMinute = Duration.ofMinutes(1);
        Duration twentyFourHours = Duration.ofHours(24);

        if (totalTime.isNegative() ||
                totalTime.compareTo(oneMinute) < 0 ||
                totalTime.compareTo(twentyFourHours) >= 0) {
            throw new BadRequestException("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다");
        }
    }

    private static void validateDate(final LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new BadRequestException("날짜는 오늘 이후여야 합니다.");
        }
    }

    public void validateIsMine(final UUID memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new ForbiddenException("해당 목표에 접근할 권한이 없습니다.");
        }
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
        validateDate(date);
        this.date = date;
    }

    public void changeStatusToStarted() {
        if (this.status == GoalStatus.BEFORE_STARTING || this.status == GoalStatus.PAUSED) {
            this.status = GoalStatus.RUNNING;
        } else {
            throw new ConflictException("스톱워치가 이미 실행되거나 기간이 지난 상태입니다.");
        }
    }

    public void changeStatusToPaused() {
        if (this.status == GoalStatus.RUNNING) {
            this.status = GoalStatus.PAUSED;
        } else {
            throw new ConflictException("스톱워치가 이미 정지되었습니다.");
        }
    }

    public void addDuration(final Duration addedDuration) {
        this.duration = this.duration.plus(addedDuration);
    }
}