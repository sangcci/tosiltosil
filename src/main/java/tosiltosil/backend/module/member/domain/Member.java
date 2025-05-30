package tosiltosil.backend.module.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.module.member.domain.value.ActiveStatus;
import tosiltosil.backend.module.member.domain.value.LoginType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // 프로필 설정 방식 고려해야 함
    @Column(nullable = true)
    private String nickname;

    // 인증 코드 로직 만들면 false로 변경
    @Column(nullable = true)
    private String code;

    // 프로필 설정 방식 고려해야 함
    @Column(nullable = true)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean visibility;

    @Enumerated(EnumType.STRING)
    private ActiveStatus stopwatchStatus;

    @Builder
    private Member(
            final String nickname,
            final String code,
            final String profileImageUrl,
            final LoginType loginType,
            final String email
    ) {
        this.nickname = nickname;
        this.code = code;
        this.profileImageUrl = profileImageUrl;
        this.loginType = loginType;
        this.email = email;
        this.visibility = true;
        this.stopwatchStatus = ActiveStatus.INACTIVE;
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
                .build();
    }
}