package tosiltosil.backend.module.progress.domain;

import jakarta.persistence.*;
import lombok.*;
import tosiltosil.backend.common.domain.BaseEntity;

import java.time.Duration;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Progress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Transient
    @Setter // for mapping
    private Duration todayDuration;

    @Column(nullable = false)
    private int totalGoalAchievedCount;

    @Builder
    private Progress(
            final UUID memberId,
            final Duration todayDuration,
            final int totalGoalAchievedCount
    ) {
        this.memberId = memberId;
        this.todayDuration = (todayDuration == null) ? Duration.ZERO : todayDuration;
        this.totalGoalAchievedCount = totalGoalAchievedCount;
    }

    public static Progress of(
            final UUID memberId,
            final Duration todayDuration,
            final int totalGoalAchievedCount
    ) {
        return Progress.builder()
                .memberId(memberId)
                .todayDuration(todayDuration)
                .totalGoalAchievedCount(totalGoalAchievedCount)
                .build();
    }

    public void addTodayDuration(final Duration duration) {
        this.todayDuration = this.todayDuration.plus(duration);
    }

    public void subtractTodayDuration(final Duration duration) {
        Duration totalTime = this.todayDuration.minus(duration);
        if (totalTime.isNegative()) {
            this.todayDuration = Duration.ZERO;
        } else {
            this.todayDuration = totalTime;
        }
    }
}
