package tosiltosil.backend.module.auth.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.terms.application.TermsService;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private TermsService termsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 일반_로그인_성공() {
        // given
        UUID memberId = UUID.randomUUID();
        String email = "test@example.com";
        String encryptedPassword = "$2a$10$encoded-password";

        Member member = mock(Member.class);
        when(member.getId()).thenReturn(memberId);

        LocalLoginRequest request = new LocalLoginRequest(email, "password123");
        TokenPair tokenPair = new TokenPair("access-token", "refresh-token");

        when(memberService.findByEmail(email)).thenReturn(member);
        when(memberService.findPasswordByMemberId(memberId)).thenReturn(encryptedPassword);
        when(passwordEncoder.matches(request.password(), encryptedPassword)).thenReturn(true);
        when(jwtTokenProvider.createTokenPair(memberId)).thenReturn(tokenPair);

        // when
        LocalLoginResponse response = authService.localLogin(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }
}