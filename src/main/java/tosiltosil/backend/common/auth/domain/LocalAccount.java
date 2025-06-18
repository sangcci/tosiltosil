package tosiltosil.backend.common.auth.domain;

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
public class LocalAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false, length = 60)
    private String password;

    @Builder
    private LocalAccount(
            final UUID memberId,
            final String password
    ) {
        this.memberId = memberId;
        this.password = password;
    }

    public static LocalAccount of(
            final UUID memberId,
            final String encryptedPassword
    ) {
        return LocalAccount.builder()
                .memberId(memberId)
                .password(encryptedPassword)
                .build();
    }
}