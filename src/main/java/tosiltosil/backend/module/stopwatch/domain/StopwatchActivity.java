package tosiltosil.backend.module.stopwatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.module.stopwatch.domain.value.StopwatchStatus;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StopwatchActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StopwatchStatus stopwatchStatus;

    @Builder
    private StopwatchActivity(
            final UUID memberId,
            final StopwatchStatus stopwatchStatus
    ) {
        this.memberId = memberId;
        this.stopwatchStatus = stopwatchStatus;
    }

    public static StopwatchActivity of(final UUID memberId) {
        return StopwatchActivity.builder()
                .memberId(memberId)
                .stopwatchStatus(StopwatchStatus.INACTIVE)
                .build();
    }
    
    // change status
    public void changeToActive() {
        this.stopwatchStatus = StopwatchStatus.ACTIVE;
    }

    public void changeToInactive() {
        this.stopwatchStatus = StopwatchStatus.INACTIVE;
    }
}
