package tosiltosil.backend.module.duration.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UniqueMemberIdAndDate", columnNames = {"memberId", "date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyDuration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private Duration time;

    @Column(nullable = false)
    private LocalDateTime date;

    @Builder
    private DailyDuration(final UUID memberId, final Duration time, final LocalDateTime date) {
        this.memberId = memberId;
        this.time = time;
        this.date = date;
    }

    /**
     * 오전 5시 기준 new day
     * 누적 시간은 0, 날짜는 오늘 날짜 + 오전 5시 고정
     */
    public static DailyDuration of(final UUID memberId) {
        return DailyDuration.builder()
                .memberId(memberId)
                .time(Duration.ZERO)
                .date(LocalDate.now().atTime(LocalTime.of(5,0)))
                .build();
    }
}
