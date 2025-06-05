package tosiltosil.backend.common.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.common.auth.domain.value.SocialProvider;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;

    @Builder
    private SocialAccount(
            final UUID memberId,
            final String oauthId,
            final SocialProvider provider
    ) {
        this.memberId = memberId;
        this.oauthId = oauthId;
        this.provider = provider;
    }
}
