package tosiltosil.backend.module.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTotalTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private Duration time;

    @Column(nullable = false)
    private LocalDateTime date;

    @Builder
    private DailyTotalTime(final UUID memberId, final Duration time, final LocalDateTime date) {
        this.memberId = memberId;
        this.time = time;
        this.date = date;
    }

    /**
     * 오전 5시 기준 new day
     * 누적 시간은 0, 날짜는 오늘 날짜 + 오전 5시 고정
     */
    public static DailyTotalTime of(final UUID memberId) {
        return DailyTotalTime.builder()
                .memberId(memberId)
                .time(Duration.ZERO)
                .date(LocalDate.now().atTime(LocalTime.of(5,0)))
                .build();
    }

    /**
     * 24시간 보다 클 경우 예외 발생
     */
    public void validateDurationUnder24Hours() {
        final Duration max = Duration.ofHours(24);
        if (time.compareTo(max) >= 0) {
            throw new IllegalArgumentException("일일 목표 총 시간인 24시간을 초과하여 목표를 생성할 수 없습니다.");
        }
    }
}
