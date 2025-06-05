package tosiltosil.backend.module.goal.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
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
    private Long totalTime;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    private Long order;

    @Column(nullable = false)
    private Long iconId;

    @Column(nullable = false)
    private LocalDateTime date;

    @Builder
    private Goal(
            final UUID memberId,
            final Long categoryId,
            final String title,
            final Long totalTime,
            final Long iconId,
            final Long order
    ) {
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.title = title;
        this.totalTime = totalTime;
        this.status = GoalStatus.PAUSED;
        this.duration = 0L;
        this.order = order;
        this.iconId = iconId;
        this.date = LocalDateTime.now();
    }
}