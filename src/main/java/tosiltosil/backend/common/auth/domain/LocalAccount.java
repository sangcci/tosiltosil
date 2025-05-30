package tosiltosil.backend.common.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import tosiltosil.backend.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Builder
    private LocalAccount(
            final UUID memberId,
            final String email,
            final String password
    ) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
    }

    public static LocalAccount of(
            final UUID memberId,
            final String email,
            final String password
    ) {
        return LocalAccount.builder()
                .memberId(memberId)
                .email(email)
                .password(password)
                .build();
    }
}