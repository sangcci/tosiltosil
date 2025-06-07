package tosiltosil.backend.module.friend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.module.friend.domain.value.RelationshipStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Relationship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID targetMemberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    @Builder
    private Relationship(
            final UUID memberId,
            final UUID targetMemberId,
            final RelationshipStatus status
    ) {
        this.memberId = memberId;
        this.targetMemberId = targetMemberId;
        this.status = status;
    }

    public static Relationship of(
            final UUID memberId,
            final UUID targetMemberId,
            final RelationshipStatus status
    ) {
        return Relationship.builder()
                .memberId(memberId)
                .targetMemberId(targetMemberId)
                .status(status)
                .build();
    }
}