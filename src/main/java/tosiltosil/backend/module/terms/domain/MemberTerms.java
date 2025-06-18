package tosiltosil.backend.module.terms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTerms extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private Long termId;

    @Column(nullable = false)
    private boolean isAgreed;

    @Builder
    private MemberTerms(
            final UUID memberId,
            final Long termId,
            final boolean isAgreed
    ) {
        this.memberId = memberId;
        this.termId = termId;
        this.isAgreed = isAgreed;
    }

    public static MemberTerms of(
            final UUID memberId,
            final Long termId,
            final boolean isAgreed
    ) {
        return MemberTerms.builder()
                .memberId(memberId)
                .termId(termId)
                .isAgreed(isAgreed)
                .build();
    }
}
