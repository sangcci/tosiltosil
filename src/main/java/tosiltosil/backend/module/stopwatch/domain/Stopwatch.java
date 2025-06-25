package tosiltosil.backend.module.stopwatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stopwatch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long goalId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Builder
    private Stopwatch(
            final Long goalId,
            final LocalDateTime startedAt
    ) {
        this.goalId = goalId;
        this.startedAt = startedAt;
    }

    public static Stopwatch of(final Long goalId) {
        return Stopwatch.builder()
                .goalId(goalId)
                .startedAt(LocalDateTime.now())
                .build();
    }

    public void updateEndAt() {
        this.endedAt = LocalDateTime.now();
    }

    public Duration getDuration() {
        if (this.endedAt == null) {
            return Duration.ZERO;
        }
        return Duration.between(this.startedAt, this.endedAt);
    }
}