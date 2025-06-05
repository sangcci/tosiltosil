package tosiltosil.backend.module.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.module.member.domain.value.StopwatchStatus;
import tosiltosil.backend.module.member.domain.value.LoginType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean visibility;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StopwatchStatus stopwatchStatus;

    @Builder
    private Member(
            final String nickname,
            final String code,
            final String profileImageUrl,
            final LoginType loginType,
            final String email,
            final boolean visibility,
            final StopwatchStatus stopwatchStatus
    ) {
        this.nickname = nickname;
        this.code = code;
        this.profileImageUrl = profileImageUrl;
        this.loginType = loginType;
        this.email = email;
        this.visibility = visibility;
        this.stopwatchStatus = stopwatchStatus;
    }

    public static Member of(
            final String nickname,
            final String code,
            final String profileImageUrl,
            final LoginType loginType,
            final String email
    ) {
        return Member.builder()
                .nickname(nickname)
                .code(code)
                .profileImageUrl(profileImageUrl)
                .loginType(loginType)
                .email(email)
                .visibility(true)
                .stopwatchStatus(StopwatchStatus.INACTIVE)
                .build();
    }
}