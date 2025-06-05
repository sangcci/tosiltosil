package tosiltosil.backend.module.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTotalTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private Long time;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Builder
    private DailyTotalTime(
            final UUID memberId,
            final Long time,
            final LocalDateTime dateTime
    ) {
        this.memberId = memberId;
        this.time = time;
        this.dateTime = dateTime;
    }

    public static DailyTotalTime of(
            final UUID memberId,
            final Long time,
            final LocalDateTime dateTime
    ) {
        return DailyTotalTime.builder()
                .memberId(memberId)
                .time(time)
                .dateTime(dateTime)
                .build();
    }
}
