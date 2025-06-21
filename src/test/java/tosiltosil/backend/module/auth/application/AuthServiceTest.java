package tosiltosil.backend.module.auth.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.application.TermsService;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private TermsService termsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MultipartFile profileImage;

    private List<TermsDetail> createValidTerms() {
        return List.of(
                new TermsDetail("termsOfService", "0.1.0", true),
                new TermsDetail("privacyPolicy", "0.1.0", true),
                new TermsDetail("ageConfirmation", "0.1.0", true)
        );
    }

    private CreateLocalMemberRequest mockValidRequest(String email, String password, List<TermsDetail> terms) {
        CreateLocalMemberRequest request = mock(CreateLocalMemberRequest.class);
        when(request.email()).thenReturn(email);
        when(request.password()).thenReturn(password);
        when(request.terms()).thenReturn(terms);
        return request;
    }

    @Test
    void LOCAL_회원가입_성공() {
        //given
        String email = "test@example.com";
        String password = "qwer1234!";
        String encryptedPassword = "ENC_qwer1234!";
        String code = "ABC123";

        UUID memberId = UUID.randomUUID();
        List<TermsDetail> terms = createValidTerms();

        CreateLocalMemberRequest request = mockValidRequest(email, password, terms);
        Member member = mock(Member.class);
        LocalAccount localAccount = mock(LocalAccount.class);

        when(memberService.generateRandomCode()).thenReturn(code);
        when(request.toMemberEntities(eq(code), anyString())).thenReturn(member);
        when(member.getId()).thenReturn(memberId);

        when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);
        when(request.toLocalAccountEntities(eq(memberId), eq(encryptedPassword))).thenReturn(localAccount);

        // when
        authService.localSignUp(request, profileImage);

        // then
        verify(termsService).validateTerms(terms);
        verify(memberService).validateEmail(email, LoginType.LOCAL);
        verify(memberService).saveMember(member);
        verify(memberService).saveLocalAccount(localAccount);
        verify(termsService).saveTerms(memberId, terms);
    }

    @Test
    void 중복_이메일_회원가입_실패() {
        //given
        CreateLocalMemberRequest request = mock(CreateLocalMemberRequest.class);
        String email = "duplicate@example.com";

        when(request.email()).thenReturn(email);

        doThrow(new ConflictException("이미 등록된 이메일입니다."))
                .when(memberService).validateEmail(email, LoginType.LOCAL);

        // when & then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> authService.localSignUp(request, profileImage)
        );

        assertEquals("이미 등록된 이메일입니다.", exception.getMessage());
    }
}
